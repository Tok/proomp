(ns proomp.core
  (:require [proomp.util.file-util :as file-util]
            [proomp.util.image-util :as image-util]
            [proomp.util.pipe-util :as pipe-util]
            [cambium.core :as log]))

(def prompt "Caliper Remote")


(defn -main []
  (log/info {:prompt prompt})
  (let [file-name (file-util/prompt->file-name prompt)
        pipe (pipe-util/create-pipeline)
        image (pipe-util/generate-image pipe prompt)]
    (image-util/save-python-image image file-name)))
