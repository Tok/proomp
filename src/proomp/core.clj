(ns proomp.core
  (:require [proomp.constants :as const]
            [proomp.util.file-util :as file-util]
            [proomp.util.image-util :as image-util]
            [proomp.util.pipe-util :as pipe-util]
            [cambium.core :as log]))

(def prompt "Caliper Remote")


(defn -main []
  (log/info {:prompt prompt})
  (let [pipe (pipe-util/create-pipeline)]
    (doseq [seed const/seed-range]
      (log/trace {:seed seed})
      (let [file-name (file-util/->file-name prompt seed)]
        (if (not (file-util/file-exists? file-name))
          (let [image (pipe-util/generate-image pipe prompt seed)]
            (image-util/save-python-image image file-name)))
        (log/warn {:skipping file-name})))))
