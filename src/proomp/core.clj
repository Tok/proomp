(ns proomp.core
  (:require [proomp.config :as config]                      ;don't remove this
            [proomp.tools.seed-space-animator :as seed-space-animator]
            [proomp.tools.upscaler :as upscaler]
            [proomp.tools.riffusion-client :as riffusion-client]
            [proomp.domain.prompt.prompt :as prompt]
            [proomp.util.file-utils :as file-utils]
            [proomp.util.image-utils :as image-utils]
            [proomp.util.pipe-utils :as pipe-utils]
            [proomp.util.video-utils :as video-utils]
            [cambium.core :as log]))

;(defonce action ::generate-seed-images)
;(defonce action ::generate-frames)
;(defonce action ::upscale-frames)
(defonce action ::generate-audio)
;(defonce action ::frames-to-video)

(defonce text "Caliper Remote")
(defonce negative-text "")
(defonce additions "((sharp)) (focussed) (photo)")
(defonce negative-additions "(drawing) cgi blurry grayscale")
(defonce full-prompt (prompt/->Prompt text negative-text additions negative-additions))

;todo make sure seed pic exists
(defonce animation-start-seed 21)                           ;choose a good seed with ::generate-seed-images
(defonce riffusion-start-seed 0)

(defn- do-generation! [pipe seed file-name]
  (let [image (pipe-utils/generate-image pipe full-prompt seed)]
    (image-utils/save-py-image! image file-name)
    image))

(defn- generate-image! [pipe seed]
  (log/trace {:seed seed})
  (let [file-name (file-utils/file-name (:text full-prompt) seed)]
    (if (not (file-utils/file-exists? file-name))
      (do-generation! pipe seed file-name)
      (log/warn {:skip-existing file-name}))))

(defn -main []
  (condp = action
    ::generate-frames
    (let [pipe (pipe-utils/->image-to-image-pipeline)
          frame-count (* 3600 5)]
      (seed-space-animator/animate pipe full-prompt animation-start-seed frame-count))

    ::generate-seed-images
    (let [pipe (pipe-utils/->text-to-image-pipeline)
          number-of-images-to-generate 1000
          start-seed 0]
      (doseq [seed (range start-seed (+ start-seed number-of-images-to-generate))]
        (generate-image! pipe seed)))

    ::upscale-frames
    (let [up-pipe (pipe-utils/->upscaler-pipeline)]
      (upscaler/upscale-to-full-hd-portrait up-pipe full-prompt))
    ;(upscaler/upscale-to-qhd-portrait up-pipe full-prompt))

    ::generate-audio
    (do
      (riffusion-client/start-riffusion-server)
      (Thread/sleep 30000)                                  ;TODO
      (riffusion-client/post-riffusion-request full-prompt riffusion-start-seed))

    ::frames-to-video
    ;(video-utils/generate-video-from-frames (:text full-prompt))
    (video-utils/generate-video-from-upscaled-frames (:text full-prompt)))
  (log/info "Done."))
