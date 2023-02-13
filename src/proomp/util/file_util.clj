(ns proomp.util.file-util
  (:require [proomp.config :as config]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py.] :as py]))

(require-python 'pathlib)

(defonce image-format "png")
(defonce image-suffix (str "." image-format))

(defn image-dir [prompt] (str config/media-path prompt "\\"))
(defn animation-frame-dir [prompt] (str (image-dir prompt) "frames\\"))

(defn file-name [prompt seed]
  (let [padded-seed (format "%04d" seed)]
    (str (image-dir prompt) padded-seed "_" prompt image-suffix)))

(defn frame-name [prompt seed iterations scale]
  (str (animation-frame-dir prompt) "\\"
       (format "%06d" seed) "_" scale "_" iterations "_" prompt image-suffix))

(defn file-exists? [file-name] (py. (py/$c pathlib/Path file-name) "is_file"))
