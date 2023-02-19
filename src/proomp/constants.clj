(ns proomp.constants
  (:require [proomp.domain.image.resolution :as res]
            [cambium.core :as log])
  (:import (proomp.domain.image.resolution Resolution)))

;; Prompt to Image Constants
(def iterations 40)                                         ;default 40
(def scale 7.5)                                             ;default 7.5
(log/debug {:iterations iterations :scale scale})

;; Animation Constants
(def ani-iterations 40)                                     ;default 40
(def ani-scale 7.5)                                         ;default 7.5
(def ani-noise 0.50)                                        ;default 0.55
(log/debug {:ani-iterations ani-iterations :ani-scale ani-scale :ani-noise :ani-noise})

(def ^:private repetitions 100)
(defn seed-range [start-seed] (range start-seed (+ start-seed repetitions)))
(log/debug {:repetitions repetitions :seed-range seed-range})
