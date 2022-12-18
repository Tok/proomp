(ns proomp.util.image-util
  (:require [cambium.core :as log]
            [libpython-clj2.python :refer [py.] :as py]))

(defn save-python-image [image file-name]
  (log/debug {:file-name file-name})
  (py. image "save" file-name))
