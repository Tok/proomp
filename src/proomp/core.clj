(ns proomp.core
  (:require [proomp.constants :as const]
            [proomp.config :as config]                      ;don't remove this
            [proomp.animator :as animator]
            [proomp.util.file-util :as file-util]
            [proomp.util.image-util :as image-util]
            [proomp.util.pipe-util :as pipe-util]
            [proomp.util.video-util :as video-util]
            [cambium.core :as log]))
(defonce ^:private modes [::animation ::images ::video])

(defonce prompt "Caliper Remote")
(defonce neg-prompt "") ;also see additions in proomp.constants
(defonce animation-start-seed 0) ;choose a good seed by generating ::images first

;;select mode:
;(defonce mode ::images)
(defonce mode ::animation)
;(defonce mode ::video)

(defn- do-generation! [pipe seed file-name]
  (let [image (pipe-util/generate-image pipe prompt neg-prompt seed)]
    (image-util/save-py-image! image file-name)))

(defn- generate-image! [pipe seed]
  (log/trace {:seed seed})
  (let [file-name (file-util/file-name prompt seed)]
    (if (not (file-util/file-exists? file-name))
      (do-generation! pipe seed file-name)
      (log/warn {:skip-existing file-name}))))

(defn -main []
  (log/info {:mode mode :prompt prompt})
  (if (= mode ::animation)
    (let [pipe (pipe-util/->image-to-image-pipeline)]
      (animator/animate pipe prompt neg-prompt animation-start-seed))
    (if (= mode ::images)
      (let [pipe (pipe-util/->text-to-image-pipeline)]
        (doseq [seed (const/seed-range animation-start-seed)]
          (generate-image! pipe seed)))
      (video-util/generate-video-from-frames prompt))))
