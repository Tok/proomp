(ns proomp.animator
  (:require
    [proomp.constants :as const]
    [proomp.util.file-util :as file-util]
    [proomp.util.image-util :as image-util]
    [proomp.util.pipe-util :as pipe-util]
    [clojure.java.io :as io]
    [cambium.core :as log]
    [libpython-clj2.require :refer [require-python]]
    [libpython-clj2.python :refer [py.] :as py]))

(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[PIL.Image :refer [open new]])
(require-python '[PIL.ImageChops :as chops])

(def dim {:w 960 :h 540})                                   ;19:6 aspect ratio, half of "Full HD"
(def regenerate-1st-frame? false)
(def frame-count (* 3600 5))
(def iterations const/iterations)                           ;default 50
(def noise 0.5)                                             ;0.0 to 1.0, default 0.5

(def rotation-degree 0.0)
(def x-offset -0)
(def y-offset 0)
(def zoom 1.015)


;;FIXME
(defn animate [pipe prompt neg-prompt start-seed]
  (log/info {:frame-count frame-count :iterations iterations :noise noise})
  (let [path (file-util/->animation-frame-directory-name prompt)
        w const/ani-w
        h const/ani-h
        iterations const/iterations
        scale const/ani-scale]
    (log/debug {:path path})
    (io/make-parents path)
    (let [first-frame-file-name (file-util/->frame-name prompt start-seed iterations scale)
          image (if regenerate-1st-frame?
                  (let [canvas-image (PIL.Image/new "RGB" [w h])]
                    (log/info {:canvas-image canvas-image})
                    (py. canvas-image "save" :frame_file_name "PNG")
                    (let [image (pipe-util/generate-i2i pipe prompt neg-prompt start-seed canvas-image)]
                      (py. image "save" :frame_file_name "PNG")
                      image))
                  (let [start-image first-frame-file-name]
                    (py/with [im (PIL.Image/open start-image)]
                             (let [converted (py. im "convert" "RGB")
                                   image (py. converted "resize" [w h])]
                               (py. image "save" :frame_file_name "PNG")
                               image))))]
      (let [reference-image (image-util/select-reference-image image w h)
            ;skip transformations for the initial image
            apply-transformations? false]
        (doseq [frame-number (range 0 frame-count)]
          ;the initial image will be regenerated with referenced color correction
          (let [new-seed (+ start-seed frame-number)
                frame-file-name (file-util/->frame-name prompt new-seed iterations scale)]
            (if (not (file-util/file-exists? frame-file-name))
              (if apply-transformations?
                (let [rotated (py. image "rotate" rotation-degree)
                      chopped (chops/offset rotated x-offset y-offset)]
                  (py. chopped "zoom_center" zoom))
                (let [color-fixed (py. image "fix_colors" zoom reference-image)
                      sample (pipe-util/generate-i2i pipe prompt neg-prompt new-seed color-fixed)
                      resized (py. sample "resize")]
                  (image-util/save-python-image image frame-file-name)))
              (log/warn {:skip-existing frame-file-name}))))))))
