(ns proomp.util.file-util
  (:require [proomp.config :as config]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py.] :as py]))

(require-python 'pathlib)

(defonce image-format "png")
(defonce image-suffix (str "." image-format))

(defn ->file-name [prompt seed]
  (let [padded-seed (format "%04d" seed)
        image-path (str config/media-path prompt "\\")]
    (str image-path padded-seed "_" prompt image-suffix)))

(defn ->animation-frame-directory-name [prompt]
  (str config/media-path prompt "\\animation\\frames"))

(defn ->frame-name [prompt seed iterations scale]
  (str ->animation-frame-directory-name "\\"
       seed "_" scale "_" iterations "_" prompt image-suffix))

(defn file-exists? [file-name] (py. (py/$c pathlib/Path file-name) "is_file"))
