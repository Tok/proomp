(ns proomp.util.image-util
  (:require [cambium.core :as log]
            [clojure.java.io :as io]
            [libpython-clj2.python :refer [py. py.-] :as py]
            [libpython-clj2.require :refer [require-python]]
            [proomp.config :as config]
            [proomp.constants :as const]
            [proomp.util.file-util :as file-util]))

(require-python 'PIL '[PIL.Image :as pilimg])
(require-python '[numpy :as np])

(require-python '[skimage.exposure :refer [match_histograms]])

(defn new-py-image [w h] (PIL.Image/new "RGB" [w h]))
(defn open-py-image [file-name] (py. (PIL.Image/open file-name) "convert" "RGB"))
(defn save-py-image! [image file-name]
  (if (file-util/file-exists? file-name)
    (log/warn {"File exists. Skipping." file-name})
    (do
      (io/make-parents file-name)
      (log/debug {"Saving image." file-name})
      (py. image "save" file-name))))

(defn resize [image w h]
  (py. image "resize" (py/->py-tuple [w h])))

(defn zoom-center [image zoom]
  (let [w (py.- image :size)
        h (py.- image :size)
        x (* w 0.5)
        y (* h 0.5)
        double-zoom (* zoom 2)
        top-left (/ (- x w) / double-zoom)
        top-right (/ (- y h) / double-zoom)
        bottom-left (/ (+ x w) / double-zoom)
        bottom-right (/ (+ y h) / double-zoom)
        edges (top-left top-right bottom-left bottom-right)
        cropped (py. image "crop" edges)]
    (resize cropped w h)))

(defn numpy->pil [np-image] (PIL.Image/fromarray (py. np-image "astype" "uint8") "RGB"))
(defn fix-colors [image reference-image]
  (let [np-image (py/$c np/array image :dtype np/uint8)
        np-reference (py/$c np/array reference-image :dtype np/uint8)
        np-corrected (py/$c match_histograms np-image np-reference :channel_axis 0)]
    (numpy->pil np-corrected)))

(def ^:private fix-color-palette-to-1st-frame? false)       ;otherwise use reference image

(defn prepare-reference-image [image]
  (let [ref-file (str config/image-path "DefaultHistogramReference.png")
        ref-img (py. (pilimg/open ref-file) "convert" "RGB")]
    (resize (if fix-color-palette-to-1st-frame? image ref-img) const/ani-w const/ani-h)))
