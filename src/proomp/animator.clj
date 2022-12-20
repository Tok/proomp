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
(def regenerate-1st-frame? false)
(def frame-count (* 3600 5))

(def rotation-degree -2.0)
(def x-offset -0)
(def y-offset 0)
(def zoom 1.015)


;;FIXME
(defn animate [pipe prompt neg-prompt start-seed]
  (log/info {:dimensions dim :frame-count frame-count})
  (let [result-dir (file-util/->animation-frame-directory-name prompt)
        w const/ani-w h const/ani-h
        iterations const/iterations
        scale const/ani-scale]
    (log/debug {:result-path result-dir})
    (io/make-parents result-dir)
    (let [first-frame-file-name (file-util/->frame-name prompt start-seed iterations scale)
          image (if regenerate-1st-frame?
                  (let [canvas-image (image-util/new-py-image w h)]
                    (image-util/save-py-image canvas-image first-frame-file-name) ;TODO remove
                    (let [image (pipe-util/generate-i2i pipe prompt neg-prompt start-seed canvas-image)]
                      (image-util/save-py-image image first-frame-file-name)
                      image))
                  (py/with [im (image-util/open-py-image first-frame-file-name)]
                           (let [image (image-util/resize im w h)]
                             (log/info "Saving 1st frame.")
                             (image-util/save-py-image image first-frame-file-name)
                             image)))]
      (log/info "Loading reference image.")
      (let [reference-image (image-util/select-reference-image image w h)
            ;skip transformations for the initial image
            apply-transformations? false]
        (doseq [frame-number (range 0 frame-count)]
          ;the initial image is regenerated with referenced color correction
          (let [new-seed (+ start-seed frame-number)
                frame-file-name (file-util/->frame-name prompt new-seed iterations scale)]
            (if (file-util/file-exists? frame-file-name)
              (log/warn {:skip-existing frame-file-name})
              (let [pic (if apply-transformations?          ;FIXME test
                          (let [rotated (py. image "rotate" rotation-degree)
                                chopped (chops/offset rotated x-offset y-offset)]
                            (py. chopped "zoom_center" zoom))
                          image)]
                (let [color-fixed (image-util/fix-colors pic reference-image)
                      sample (pipe-util/generate-i2i pipe prompt neg-prompt new-seed color-fixed)
                      resized (image-util/resize sample w h)]
                  (image-util/save-py-image resized frame-file-name))))))))))
