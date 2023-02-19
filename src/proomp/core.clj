(ns proomp.core
  (:require [proomp.config :as config]                      ;don't remove this
            [proomp.animator.seed-space-animator :as seed-space-animator]
            [proomp.domain.prompt.prompt :as prompt]
            [proomp.util.file-utils :as file-utils]
            [proomp.util.image-utils :as image-utils]
            [proomp.util.pipe-utils :as pipe-utils]
            [proomp.util.video-utils :as video-utils]
            [cambium.core :as log]))

(defonce ^:private modes [::animation ::images ::video])
(defonce active-mode ::images)
;(defonce active-mode ::animation)
;(defonce active-mode ::video)

(defonce text "Caliper Remote")
(defonce negative-text "")
(defonce additions "((photorealistic)) (photo) sharp focused")
(defonce negative-additions "((blurry)) (drawing) grayscale deformed disfigured")
(defonce full-prompt (prompt/->Prompt text negative-text additions negative-additions))

(defonce start-seed 0)
(defonce number-of-images-to-generate 1000)

(defonce animation-start-seed 0)                            ;choose a good seed by generating ::images first

(defn- do-generation! [pipe seed file-name]
  (let [image (pipe-utils/generate-image pipe full-prompt seed)]
    (image-utils/save-py-image! image file-name)))

(defn- generate-image! [pipe seed]
  (log/trace {:seed seed})
  (let [file-name (file-utils/file-name (:text full-prompt) seed)]
    (if (not (file-utils/file-exists? file-name))
      (do-generation! pipe seed file-name)
      (log/warn {:skip-existing file-name}))))

(defn -main []
  (if (= active-mode ::animation)
    (let [pipe (pipe-utils/->image-to-image-pipeline)]
      (seed-space-animator/animate pipe full-prompt animation-start-seed))
    (if (= active-mode ::images)
      (let [pipe (pipe-utils/->text-to-image-pipeline)]
        (doseq [seed (range start-seed (+ start-seed number-of-images-to-generate))]
          (generate-image! pipe seed)))
      (video-utils/generate-video-from-frames (:text full-prompt)))))
