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

(require-python 'PIL '[PIL.ImageChops :as chops])


(def dim {:w 960 :h 540})                                   ;19:6 aspect ratio, half of "Full HD"
(def regenerate-1st-frame false)
(def frame-count (* 3600 5))
(def iterations const/iterations)                           ;default 50
(def noise 0.5)                                             ;0.0 to 1.0, default 0.5

(def rotation-degree 0.0)
(def x-offset -0)
(def y-offset 0)
(def zoom 1.015)

;;FIXME
(defn animate [w h prompt start-seed iterations scale]
  (log/info {:frame-count frame-count :iterations iterations :noise noise})
  (let [pipe (pipe-util/->image-to-image-pipeline)
        path (file-util/->animation-frame-directory-name prompt)]
    (io/make-parents path)
    (let [first-frame-file-name (file-util/->frame-name prompt start-seed iterations scale)]
      (if regenerate-1st-frame?
        (let [canvas-image (Image/New :mode "RGB" :size {w h} :color "#7F7F7F")]
          (py. canvas-image "save" :frame_file_name "PNG")
          (let [image (create-sample-i2i pipe prompt start-seed scale iterations 1.0 canvas-image)]
            (py. image "save" :frame_file_name "PNG")))
        (let [start-image first-frame-file-name]
          (py/with [im (py/py.- Image/open start-image)]
                   (let [converted (py. im "convert" "RGB")
                         image (py. converted "resize" {w h})]
                     (py. image "save" :frame_file_name "PNG")))))
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
                      sample (create-sample-i2i pipe prompt new-seed scale iterations noise color-fixed)
                      resized (py. sample "resize")]
                  (image/save frame-file-name "PNG")))
              (log/warn {:skip-existing frame-file-name}))))))))
