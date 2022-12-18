(ns proomp.util.file-util
  (:require [proomp.config :as config]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py.] :as py]))

(require-python 'pathlib)

(defonce image-format "png")
(defonce image-suffix (str "." image-format))

(defn ->file-name [prompt seed] (str config/media-path seed "_" prompt image-suffix))
(defn file-exists? [file-name] (py. (py/$c pathlib/Path file-name) "is_file"))
