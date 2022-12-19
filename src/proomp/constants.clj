(ns proomp.constants
  (:require [cambium.core :as log]))

(defonce w 768)                                             ;default 768
(defonce h 768)                                             ;default 768
(def scale 7.5)                                             ;default 7.5
(def iterations 40)                                         ;default 50
(def start-seed 0)
(def ^:private repetitions 100)
(def seed-range (range start-seed (+ start-seed repetitions)))

;; Prompt Constants
(def prompt-addition "((photorealistic)) (photo) focused professional")

(def neg-prompt-addition "(blurry) (drawing) comic bad grayscale deformed disfigured")



;; Animation Constants
(def ani-start-seed 34)
; 960 and 540 is a 19:6 aspect ratio, half of "Full HD"
(defonce ani-w 960)                                         ;default 960
(defonce ani-h 540)                                         ;default 540
(def ani-scale 5.5)                                         ;default 5.5
(def ani-noise 1.0)                                         ;default 1.0

(log/debug {:width w :height h :iterations iterations :scale scale})
(log/debug {:ani-width ani-w :ani-height ani-h :ani-scale ani-scale})
