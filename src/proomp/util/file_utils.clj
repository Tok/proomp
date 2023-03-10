(ns proomp.util.file-utils
  (:require [proomp.config :as config]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py.] :as py]))

(require-python 'pathlib)

(defonce image-format "png")
(defonce image-suffix (str "." image-format))

(defn image-dir [prompt] (str config/media-path prompt "\\"))
(defn animation-frame-dir [prompt] (str (image-dir prompt) "frames\\"))
(defn upscaled-frame-dir [prompt] (str (image-dir prompt) "frames\\upscaled\\"))
(defn audio-dir [prompt] (str (image-dir prompt) "riffusion\\audio\\"))
(defn spectrogram-dir [prompt] (str (image-dir prompt) "riffusion\\spectrogram\\"))

(defn file-name [prompt seed]
  (let [padded-seed (format "%04d" seed)]
    (str (image-dir prompt) padded-seed "_" prompt image-suffix)))

(defn frame-name [prompt seed iterations scale]
  (str (animation-frame-dir prompt) "\\"
       (format "%06d" seed) "_" scale "_" iterations "_" prompt image-suffix))

(defn audio-name [prompt-text step]
  (str (audio-dir prompt-text) "\\" (format "%04d" step) ".mp3"))

(defn spectrogram-name [prompt-text step]
  (str (spectrogram-dir prompt-text) "\\" (format "%04d" step) image-suffix))

(defn file-exists? [file-name] (py. (py/$c pathlib/Path file-name) "is_file"))
