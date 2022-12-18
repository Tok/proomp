(ns proomp.util.image-util
  (:require [clojure.java.io :as io]
            [cambium.core :as log]
            [proomp.config :as config]
            [proomp.util.file-util :as file-util]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.-] :as py]))

(require-python 'PIL '[PIL.Image :as pilimg])
(require-python '[numpy :as np])
(require-python '[skimage.exposure :refer [match_histograms]])


(defn save-python-image [image file-name]
  (if (file-util/file-exists? file-name)
    (log/warn {"File exists. Skipping." file-name})
    (do
      (io/make-parents file-name)
      (log/debug {"Saving file." file-name})
      (py. image "save" file-name))))

(defn resize [image w h] (py. image "resize" {w h}))

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

(defn- fix-colors [image reference-image]
  (let [np-image (py. np/array image :dtype np/uint8)
        np-reference (py. np/array reference-image :dtype np/uint8)
        np-corrected (match_histograms np-image np-reference :multichannel true)]
    (pilimg/fromarray np-corrected "RGB")))

(def ^:private fix-color-palette-to-1st-frame? false)       ;otherwise use reference image
(defn select-reference-image [image w h]
  (if fix-color-palette-to-1st-frame?
    (py. image "resize" {w h})
    (let [reference-image (pilimg/open (str config/image-path "DefaultHistogramReference.png"))
          converted (py. reference-image "convert" "RGB")]
      (resize converted w h))))
