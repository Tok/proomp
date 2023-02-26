(ns proomp.tools.upscaler
  (:require
    [cambium.core :as log]
    [proomp.config :as config]
    [clojure.java.io :as io]
    [proomp.util.file-utils :as file-utils]
    [proomp.util.image-utils :as image-utils]
    [proomp.util.pipe-utils :as pipe-utils]))

(defn- upscale [up-pipe prompt from to]
  "Upscales frames in the frame directory, matching the current prompt."
  (let [prompt-text (:text prompt)
        frame-path (file-utils/animation-frame-dir prompt-text)
        frame-dir (clojure.java.io/file frame-path)
        frames (.listFiles (io/file frame-dir))
        upscaled-path (file-utils/upscaled-frame-dir prompt-text)
        upscaled-dir (clojure.java.io/file upscaled-path)]
    (log/info {:prompt-text prompt-text})
    (doseq [frame frames]
      (let [pil-image (image-utils/open-py-image (.getAbsolutePath frame))
            resized (image-utils/resize pil-image (:w from) (:h from))
            upscaled-frame-file (clojure.java.io/file upscaled-dir (.getName frame))
            upscaled-frame-file-name (.getAbsolutePath upscaled-frame-file)]
        (if (file-utils/file-exists? upscaled-frame-file-name)
          (log/warn {:skip-existing upscaled-frame-file-name})
          (let [upscaled (pipe-utils/generate-upscale up-pipe prompt resized)
                resized-up (image-utils/resize upscaled (:w to) (:h to))]
            (image-utils/save-py-image! resized-up upscaled-frame-file-name)))))
    (log/info {:done "Upscaling completed."})))

(defn upscale-to-full-hd-landscape [up-pipe prompt]
  (upscale up-pipe prompt {:w 480 :h 270} {:w 1920 :h 1080}))
(defn upscale-to-full-hd-portrait [up-pipe prompt]
  (upscale up-pipe prompt {:w 270 :h 480} {:w 1080 :h 1920}))

(defn upscale-to-qhd-landscape [up-pipe prompt]             ;slow
  (upscale up-pipe prompt {:w 640 :h 360} {:w 2560 :h 1440}))
(defn upscale-to-qhd-portrait [up-pipe prompt]
  (upscale up-pipe prompt {:w 360 :h 640} {:w 1440 :h 2560}))

(defn upscale-to-4k-uhd-landscape [up-pipe prompt]          ;requires more than 10GB VRAM
  (upscale up-pipe prompt {:w 960 :h 540} {:w 3840 :h 2160}))
(defn upscale-to-4k-uhd-portrait [up-pipe prompt]
  (upscale up-pipe prompt {:w 540 :h 960} {:w 2160 :h 3840}))
