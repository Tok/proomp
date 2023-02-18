(ns proomp.constants
  (:require [proomp.domain.resolution :as res]
            [cambium.core :as log])
  (:import (proomp.domain.resolution Resolution)))

(defonce ^Resolution image-resolution (res/resolutions :HD-landscape))
(def scale 7.5)                                             ;default 7.5
(def iterations 40)                                         ;default 40


(def ^:private repetitions 100)
(defn seed-range [start-seed] (range start-seed (+ start-seed repetitions)))

;; Prompt Constants
(def prompt-addition "((photorealistic)) (photo) sharp focused")

(def neg-prompt-addition "((blurry)) (drawing) grayscale deformed disfigured")


;; Animation Constants
; 960 and 540 is a 19:6 aspect ratio, half of "Full HD"
(defonce ^Resolution animation-resolution (res/resolutions :HD-landscape))
(def ani-iterations 40)                                     ;default 40
(def ani-scale 7.5)                                         ;default 7.5
(def ani-noise 0.50)                                        ;default 0.55


(log/debug {:image-resolution image-resolution})
(log/debug {:iterations iterations :scale scale})
(log/debug {:animation-resolution animation-resolution})
(log/debug {:ani-iterations ani-iterations :ani-scale ani-scale :ani-noise :ani-noise})
