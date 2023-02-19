(ns proomp.util.image-utils
  (:require [cambium.core :as log]
            [clojure.java.io :as io]
            [libpython-clj2.python :refer [py. py.-] :as py]
            [libpython-clj2.require :refer [require-python]]
            [proomp.config :as config]
            [proomp.domain.image.resolution :as res]
            [proomp.util.file-utils :as file-utils])
  (:import (java.awt RenderingHints)
           (java.awt.image BufferedImage RenderedImage)
           (java.io ByteArrayOutputStream File IOException)
           (javax.imageio ImageIO)))

(require-python '[builtins :as python])
(require-python '[io :as pyio])
(require-python 'PIL '[PIL.Image :as pilimg])
(require-python '[numpy :as np])
(require-python '[numpy.ndarray :as ndarray])
(require-python '[skimage.exposure :refer [match_histograms]])
(defn new-py-image [w h] (PIL.Image/new "RGB" [w h]))
(defn open-py-image [file-name] (py. (PIL.Image/open file-name) "convert" "RGB"))

(defn- do-py-image-save! [image file-name]
  (io/make-parents file-name)
  (log/debug {"Saving image." file-name})
  (py. image "save" file-name "PNG"))

(defn save-py-image!
  ([image file-name] (save-py-image! image file-name false))
  ([image file-name overwrite?]
   (if overwrite?
     (do-py-image-save! image file-name)
     (if (file-utils/file-exists? file-name)
       (log/warn {"File exists. Skipping." file-name})
       (do-py-image-save! image file-name)))))

(defn ^BufferedImage open-buffered-image-file [^File file] (ImageIO/read file))
(defn ^BufferedImage open-buffered-image [^String file-name]
  (open-buffered-image-file (File. file-name)))

(defn resize [image w h]
  (py. image "resize" (py/->py-tuple [w h])))
(defn crop [image left top right bottom]
  (py. image "crop" (py/->py-tuple [left top right bottom])))

(defn pil->numpy [pil-image] (py/$c np/array pil-image :dtype np/uint8))
(defn numpy->pil [np-image] (PIL.Image/fromarray (py. np-image "astype" "uint8") "RGB"))
(defn fix-colors [pil-image reference-image]
  (let [np-image (pil->numpy pil-image)
        np-reference (pil->numpy reference-image)
        np-corrected (py/$c match_histograms np-image np-reference :channel_axis 0)]
    (numpy->pil np-corrected)))

(def ^:private fix-color-palette-to-1st-frame? false)       ;otherwise use reference image
(defn prepare-reference-image [image]
  (let [resolution res/active-animation-resolution
        ref-file (str config/image-path "DefaultHistogramReference.png")
        ref-img (py. (pilimg/open ref-file) "convert" "RGB")]
    (resize (if fix-color-palette-to-1st-frame? image ref-img) (:w resolution) (:h resolution))))

(defn ^RenderedImage ->image [w h] (BufferedImage. w h BufferedImage/TYPE_INT_RGB))
(defn ^RenderedImage ->pil-image [w h] (PIL.Image/new "RGB" [w h]))

(defn image-from-template [source]
  (let [w (.getWidth source)
        h (.getHeight source)
        type (.getType source)]
    (BufferedImage. w h type)))

(defn perform-op [source op]
  (let [target (image-from-template source)]
    (.filter op source target)
    target))

(defn ^RenderedImage scale [image factor]
  (let [w (.getWidth image)
        h (.getHeight image)
        type (.getType image)
        scaled-w (* factor w)
        scaled-h (* factor h)
        scaled (BufferedImage. scaled-w scaled-h type)
        ctx (.createGraphics scaled)
        render-key RenderingHints/KEY_INTERPOLATION
        render-value RenderingHints/VALUE_INTERPOLATION_BILINEAR]
    (.setRenderingHint ctx render-key render-value)
    (.drawImage ctx image 0 0 scaled-w scaled-h 0 0 w h nil)
    (.dispose ctx)
    scaled))

(defn image->bytes [^RenderedImage image]
  (let [os (ByteArrayOutputStream.)]
    (ImageIO/write image "png" os)
    (.toByteArray os)))

(defn pil->bi [pil-image]
  (let [temp-dir (new File config/temp-media-path)]
    (.mkdir temp-dir)
    (let [file (File/createTempFile "temp" ".png" temp-dir)
          file-name (.getPath file)]                        ;todo rewrite without temp file.
      (save-py-image! pil-image file-name true)
      (let [buffered-image (open-buffered-image file-name)]
        (try
          (io/delete-file file-name)
          (catch IOException e (.getMessage e)))
        buffered-image))))

(defn zoom-center [image zoom]
  (let [w (first (py.- image :size))
        h (second (py.- image :size))
        x (* w 0.5)
        y (* h 0.5)
        cropped-width (/ w zoom)
        cropped-height (/ h zoom)
        left (int (- x (/ cropped-width 2)))
        top (int (- y (/ cropped-height 2)))
        right (int (+ x (/ cropped-width 2)))
        bottom (int (+ y (/ cropped-height 2)))
        cropped-image (crop image left top right bottom)]
    (resize cropped-image w h)))
