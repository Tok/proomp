(ns proomp.animator
  (:require
    [cambium.core :as log]
    [clojure.java.io :as io]
    [libpython-clj2.python :refer [py.] :as py]
    [libpython-clj2.require :refer [require-python]]
    [proomp.constants :as const]
    [proomp.util.file-util :as file-util]
    [proomp.util.image-util :as image-util]
    [proomp.util.pipe-util :as pipe-util]))

(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[PIL.Image :refer [open new]])
(require-python '[PIL.ImageChops :as chops])

(def dim {:w 960 :h 540})                                   ;19:6 aspect ratio, half of "Full HD"
(def frame-count (* 3600 5))

(def rotation-degree -2.0)
(def x-offset -0)
(def y-offset 0)
(def zoom 1.015)
(def apply-transformations? false)

(defn- initial-frame [prompt start-seed]
  (let [initial-frame-file-name (file-util/file-name prompt start-seed)]
    (if (not (file-util/file-exists? initial-frame-file-name))
      (log/error {:initial-frame-missing initial-frame-file-name})
      (py/with [image (image-util/open-py-image initial-frame-file-name)]
               (let [resized (image-util/resize image const/ani-w const/ani-h)]
                 (image-util/save-py-image! resized initial-frame-file-name)
                 (log/info {:initial-frame-saved initial-frame-file-name})
                 resized)))))

(defn- apply-transformations [image]
  ;TODO skip transformations for the initial image and test
  (if apply-transformations?
    (let [rotated (py. image "rotate" rotation-degree)
          chopped (chops/offset rotated x-offset y-offset)]
      (image-util/zoom-center chopped zoom))
    image))

(defn- generate-image! [image reference-image pipe prompt neg-prompt new-seed frame-file-name]
  (let [pic (apply-transformations image)]
    (let [color-corrected (image-util/fix-colors pic reference-image)
          sample (pipe-util/generate-i2i pipe prompt neg-prompt new-seed color-corrected)
          resized (image-util/resize sample const/ani-w const/ani-h)]
      (image-util/save-py-image! resized frame-file-name))))

(defn- generate-frame [image ref-image pipe prompt neg-prompt start-seed]
  (doseq [frame-number (range 0 frame-count)]
    (let [new-seed (+ start-seed frame-number)
          frame-file-name (file-util/frame-name prompt new-seed const/iterations const/ani-scale)]
      (if (file-util/file-exists? frame-file-name)
        (log/warn {:skip-existing frame-file-name})
        (generate-image! image ref-image pipe prompt neg-prompt new-seed frame-file-name)))))

(defn animate [pipe prompt neg-prompt start-seed]
  (log/info {:dimensions dim :frame-count frame-count})
  (let [result-dir (file-util/animation-frame-dir prompt)]
    (log/debug {:result-path result-dir})
    (io/make-parents result-dir)
    (let [image (initial-frame prompt start-seed)]
      (log/info "Loading reference image.")
      (let [ref-image (image-util/prepare-reference-image image)]
        (generate-frame image ref-image pipe prompt neg-prompt start-seed)))))
