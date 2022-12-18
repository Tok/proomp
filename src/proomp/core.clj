(ns proomp.core
  (:require [proomp.constants :as const]
            [proomp.util.file-util :as file-util]
            [proomp.util.image-util :as image-util]
            [proomp.util.pipe-util :as pipe-util]
            [cambium.core :as log]))

(def prompt "Caliper Remote")

(defn- do-generation! [pipe seed file-name]
  (let [image (pipe-util/generate-image pipe prompt seed)]
    (image-util/save-python-image image file-name)))
(defn- generate-frame! [pipe seed]
  (log/trace {:seed seed})
  (let [file-name (file-util/->file-name prompt seed)]
    (if (not (file-util/file-exists? file-name))
      (do-generation! pipe seed file-name)
      (log/warn {:skip-existing file-name}))))

(defn -main []
  (log/info {:prompt prompt})
  (let [pipe (pipe-util/->text-to-image-pipeline)]
    (doseq [seed const/seed-range]
      (generate-frame! pipe seed))))
