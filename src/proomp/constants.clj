(ns proomp.constants
  (:require [cambium.core :as log]))

(defonce w 768)                                             ;default 768
(defonce h 768)                                             ;default 768
(def scale 7.5)                                             ;default 7.5
(def iterations 40)                                         ;default 40


(def ^:private repetitions 100)
(defn seed-range [start-seed] (range start-seed (+ start-seed repetitions)))

;; Prompt Constants
(def prompt-addition "((photorealistic)) (photo) sharp focused")

(def neg-prompt-addition "((blurry)) (drawing) grayscale deformed disfigured")


;; Animation Constants
; 960 and 540 is a 19:6 aspect ratio, half of "Full HD"
(defonce ani-w 960)                                         ;default 960
(defonce ani-h 540)                                         ;default 540
(def ani-scale 7.5)                                         ;default 7.5
(def ani-noise 0.55)                                        ;default 0.55
(def ani-iterations 40)                                     ;default 40

(log/debug {:width w :height h :iterations iterations :scale scale})
(log/debug {:ani-width ani-w :ani-height ani-h :ani-scale ani-scale})
