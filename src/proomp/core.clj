(ns proomp.core
  (:require [proomp.constants :as const]
            [proomp.config :as config]                      ;don't remove this
            [proomp.animator :as animator]
            [proomp.util.file-utils :as file-utils]
            [proomp.util.image-utils :as image-utils]
            [proomp.util.pipe-utils :as pipe-utils]
            [proomp.util.video-utils :as video-utils]
            [cambium.core :as log]))

(defonce ^:private modes [::animation ::images ::video])

(defonce prompt "Caliper Remote")
(defonce neg-prompt "") ;also see additions in proomp.constants
(defonce animation-start-seed 0) ;choose a good seed by generating ::images first

;;select mode:
(defonce mode ::images)
;(defonce mode ::animation)
;(defonce mode ::video)

(defn- do-generation! [pipe seed file-name]
  (let [image (pipe-utils/generate-image pipe prompt neg-prompt seed)]
    (image-utils/save-py-image! image file-name)))

(defn- generate-image! [pipe seed]
  (log/trace {:seed seed})
  (let [file-name (file-utils/file-name prompt seed)]
    (if (not (file-utils/file-exists? file-name))
      (do-generation! pipe seed file-name)
      (log/warn {:skip-existing file-name}))))

(defn -main []
  (log/info {:mode mode :prompt prompt})
  (if (= mode ::animation)
    (let [pipe (pipe-utils/->image-to-image-pipeline)]
      (animator/animate pipe prompt neg-prompt animation-start-seed))
    (if (= mode ::images)
      (let [pipe (pipe-utils/->text-to-image-pipeline)]
        (doseq [seed (const/seed-range animation-start-seed)]
          (generate-image! pipe seed)))
      (video-utils/generate-video-from-frames prompt))))
