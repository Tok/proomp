(ns proomp.core
  (:require [proomp.constants :as const]
            [proomp.animator :as animator]
            [proomp.util.file-util :as file-util]
            [proomp.util.image-util :as image-util]
            [proomp.util.pipe-util :as pipe-util]
            [cambium.core :as log]))

(def prompt "Caliper Remote")
(def neg-prompt "")

(defn- do-generation! [pipe seed file-name]
  (let [image (pipe-util/generate-image pipe prompt neg-prompt seed)]
    (image-util/save-python-image image file-name)))

(defn- generate-image! [pipe seed]
  (log/trace {:seed seed})
  (let [file-name (file-util/->file-name prompt seed)]
    (if (not (file-util/file-exists? file-name))
      (do-generation! pipe seed file-name)
      (log/warn {:skip-existing file-name}))))

(defonce ^:private modes [:images :animation])
(defonce ^:private mode :images)
(defn -main []
  (log/info {:mode mode :prompt prompt})
  (if (= mode :animation)
    (let [pipe (pipe-util/->image-to-image-pipeline)]
      (animator/animate pipe prompt neg-prompt const/start-seed)) ;FIXME
    (let [pipe (pipe-util/->text-to-image-pipeline)]
      (doseq [seed const/seed-range]
        (generate-image! pipe seed)))))
