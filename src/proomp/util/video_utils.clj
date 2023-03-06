(ns proomp.util.video-utils
  (:require
    [cambium.core :as log]
    [clojure.java.io :as io]
    [proomp.util.file-utils :as file-utils]
    [proomp.util.image-utils :as image-utils])
  (:import
    (java.awt.image BufferedImage)
    (java.io ByteArrayOutputStream File)
    (java.nio.file Files)
    (javax.imageio IIOImage ImageIO ImageTypeSpecifier ImageWriter)
    (javax.imageio.metadata IIOMetadataNode IIOMetadata)
    (org.jcodec.scale AWTUtil)
    (org.jcodec.api SequenceEncoder)
    (org.jcodec.common.io NIOUtils)
    (org.jcodec.common.model ColorSpace Rational)
    (org.w3c.dom Node)))

(def ^:const frames-per-second 30)

(defn- gce? [^Node node] (-> node .getNodeName (= "GraphicControlExtension")))
(defn- find-gce [child] (if (gce? child) child (-> child .getNextSibling recur)))
(defn- find-gc-extension [root] (-> root .getFirstChild find-gce))
(defn- app-extensions []
  (let [aes (IIOMetadataNode. "ApplicationExtensions")
        ae (IIOMetadataNode. "ApplicationExtension")
        uo (byte-array [(byte 0x1) (byte 0x0) (byte 0x0)])]
    (.setAttribute ae "applicationID" "NETSCAPE")
    (.setAttribute ae "authenticationCode" "2.0")
    (.setUserObject ae uo)
    (.appendChild aes ae)
    aes))

(defn- not-gif? [mf] (not= "javax_imageio_gif_image_1.0" mf))
(defn- msg [mf] (str "Metadata format unknown: " mf))
(defn- iae [mf] (IllegalArgumentException. ^String (msg mf)))
(defn- check-mf [mf] (if (not-gif? mf) (-> mf iae throw)))
(defn- first-frame? [image-index] (= image-index 0))
(defn- configure [^IIOMetadata meta-data delay image-index]
  (let [metadata-format (.getNativeMetadataFormatName meta-data)]
    (check-mf metadata-format)
    (let [root (.getAsTree meta-data metadata-format)
          gce (find-gc-extension root)]
      (.setAttribute gce "userDelay" "FALSE")
      (.setAttribute gce "delayTime" delay)
      (if (first-frame? image-index) (.appendChild root (app-extensions)))
      (.setFromTree meta-data metadata-format root))))

(defn- write-frame [^BufferedImage src ^ImageWriter iw delay image-index]
  (let [iwp (.getDefaultWriteParam iw)
        type-spec (ImageTypeSpecifier. src)
        metadata (.getDefaultImageMetadata iw type-spec iwp)]
    (configure metadata delay image-index)
    (let [ii (IIOImage. src nil metadata)]
      (.writeToSequence iw ii nil))))

(defn- save-animate [frames delay]
  (let [format "mp4"
        ^ImageWriter iw (-> format ImageIO/getImageWritersByFormatName .next)
        os (ByteArrayOutputStream.)
        ios (ImageIO/createImageOutputStream os)]
    (.setOutput iw ios)
    (.prepareWriteSequence iw nil)
    (doseq [index (range (count frames))]
      (write-frame (nth frames index) iw delay index))
    (.endWriteSequence iw)
    (.close ios)
    (.toByteArray os)))

(def ^:const ani-step-h264 (/ 1.0 frames-per-second))
(defn animate-gif [frames]
  "Creates an animated GIF from the frames."
  (save-animate frames ani-step-h264))

(defn animate-h264 [frames ^String file-name]
  "Creates a soundless H.264/MPEG-4 AVC video from the frames."
  (let [output (File. file-name)                            ;assert .mp4
        channel (NIOUtils/writableChannel output)
        rat (Rational. frames-per-second 1)
        encoder (SequenceEncoder/createWithFps channel rat)]
    (log/info {:frames-per-second frames-per-second})
    (doseq [^BufferedImage frame frames]
      (.encodeNativeFrame
        encoder
        (AWTUtil/fromBufferedImage frame ColorSpace/RGB)))
    (.finish encoder)
    (NIOUtils/closeQuietly channel)
    (Files/readAllBytes (.toPath (.getAbsoluteFile output)))))

(defn- generate-video [prompt-text frame-path]
  (let [frame-dir (clojure.java.io/file frame-path)
        frames (map image-utils/open-buffered-image-file (.listFiles (io/file frame-dir)))
        output-file-name (str (file-utils/image-dir prompt-text) prompt-text ".mp4")]
    (log/info {:prompt-text prompt-text})
    (log/info {:frame-path frame-path})
    (log/info {:output-file-name output-file-name})
    (animate-h264 frames output-file-name)
    (log/info {:done "Video generation completed."})))

(defn generate-video-from-frames [prompt-text]
  "Generates a video from the frame directory, matching the current prompt."
  (generate-video prompt-text (file-utils/animation-frame-dir prompt-text)))

(defn generate-video-from-upscaled-frames [prompt-text]
  "Generates a video from the upscaled frame directory, matching the current prompt."
  (generate-video prompt-text (file-utils/upscaled-frame-dir prompt-text)))
