(ns proomp.constants
  (:require [proomp.domain.image.resolution :as res]
            [cambium.core :as log])
  (:import (proomp.domain.image.resolution Resolution)))

;; General Prompt Constants
(defonce prompt-addition "((photorealistic)) (photo) sharp focused")

(log/info {:prompt-additions prompt-addition})

(defonce neg-prompt-addition "((blurry)) (drawing) grayscale deformed disfigured")
(log/info {:neg-prompt-addition neg-prompt-addition})


;; Prompt to Image Constants
(defonce ^Resolution image-resolution (res/resolutions :HD-portrait))
(log/debug {:image-resolution image-resolution})

(def iterations 40)                                         ;default 40
(def scale 7.5)                                             ;default 7.5
(log/debug {:iterations iterations :scale scale})


;; Animation Constants
(defonce ^Resolution animation-resolution (res/resolutions :HD-portrait))
(log/debug {:animation-resolution animation-resolution})

(def ani-iterations 40)                                     ;default 40
(def ani-scale 7.5)                                         ;default 7.5
(def ani-noise 0.50)                                        ;default 0.55

(log/debug {:ani-iterations ani-iterations :ani-scale ani-scale :ani-noise :ani-noise})


(def ^:private repetitions 100)
(defn seed-range [start-seed] (range start-seed (+ start-seed repetitions)))
(log/debug {:repetitions repetitions :seed-range seed-range})
