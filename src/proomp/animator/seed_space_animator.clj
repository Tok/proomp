(ns proomp.animator.seed-space-animator
  (:require
    [cambium.core :as log]
    [proomp.config :as config]
    [clojure.java.io :as io]
    [libpython-clj2.python :refer [py.] :as py]
    [libpython-clj2.require :refer [require-python]]
    [proomp.domain.image.resolution :as res]
    [proomp.domain.pipe.pipe-setup :as pipe-setup]
    [proomp.util.file-utils :as file-utils]
    [proomp.util.image-utils :as image-utils]
    [proomp.util.pipe-utils :as pipe-utils]))

(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[PIL.Image :refer [open new]])
(require-python '[PIL.ImageChops :as chops])

(def rotation-degree -0.0)                                  ;per frame
(def x-offset -0)                                           ;pixels per frame
(def y-offset 0)                                            ;pixels per frame
(def zoom 1.000)                                            ;should be >= 1.000
(def apply-transformations? false)
(def apply-color-correction? false)

(defn- initial-frame [prompt start-seed initial-frame-file-name]
  (let [resolution res/active-animation-resolution]
    (if (not (file-utils/file-exists? initial-frame-file-name))
      (log/error {:initial-frame-missing initial-frame-file-name})
      (py/with [image (image-utils/open-py-image initial-frame-file-name)]
               (let [resized (image-utils/resize image (:w resolution) (:h resolution))]
                 (image-utils/save-py-image! resized initial-frame-file-name)
                 (log/info {:initial-frame-saved initial-frame-file-name})
                 resized)))))

(defn- apply-transformations [pil-image]
  (let [rotated (py. pil-image "rotate" rotation-degree)
        chopped (chops/offset rotated x-offset y-offset)]
    (image-utils/zoom-center chopped zoom)))

(defn- generate-image! [pil-image reference-image pipe prompt seed frame-file-name first-image?]
  (let [pic (if (and (not first-image?) apply-transformations?)
              (apply-transformations pil-image) pil-image)]
    (let [corrected (if apply-color-correction? (image-utils/fix-colors pic reference-image) pic)
          sample (pipe-utils/generate-i2i pipe prompt seed corrected)]
      (image-utils/save-py-image! sample frame-file-name)
      sample)))

(def last-frame (atom (image-utils/->pil-image 1 1)))       ;todo refactor to remove atom
(defn- generate-frames [pil-image ref-image pipe prompt start-seed frame-count]
  (reset! last-frame pil-image)
  (doseq [frame-number (range 0 frame-count)]
    (let [new-seed (+ start-seed frame-number)
          iterations (:iterations pipe-setup/i2i-pipe-setup)
          scale (:scale pipe-setup/i2i-pipe-setup)
          frame-file-name (file-utils/frame-name (:text prompt) new-seed iterations scale)
          first-image? (= frame-number 0)]
      (if (file-utils/file-exists? frame-file-name)
        (do
          (log/warn {:skip-existing frame-file-name})
          (reset! last-frame (image-utils/open-py-image frame-file-name)))
        (let [next-frame (generate-image! @last-frame ref-image pipe prompt new-seed frame-file-name first-image?)]
          (reset! last-frame next-frame))))))

(defn animate [pipe prompt start-seed frame-count]
  (log/info {:resolution res/active-animation-resolution :frame-count frame-count})
  (let [result-dir (file-utils/animation-frame-dir (:text prompt))]
    (log/debug {:result-path result-dir})
    (io/make-parents result-dir)
    (let [initial-frame-file-name (file-utils/file-name (:text prompt) start-seed)
          first-image (initial-frame prompt start-seed initial-frame-file-name)]
      (log/info "Loading reference image.")
      (let [ref-image (image-utils/prepare-reference-image first-image)]
        (generate-frames first-image ref-image pipe prompt start-seed frame-count)))))
