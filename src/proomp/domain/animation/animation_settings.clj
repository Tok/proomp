(ns proomp.domain.animation.animation-settings
  (:require [proomp.domain.animation.histogram-reference-image :as ref-img]
            [proomp.domain.animation.start-image :as start-img]
            [proomp.domain.animation.frame-transformation :as trans]
            [proomp.domain.image.resolution :as res]))

(defrecord AnimationSettings [resolution start-image reference-image transformation])
(def active-animation-settings
  (->AnimationSettings
    res/active-animation-resolution
    start-img/active-start-image
    ref-img/active-reference-image
    trans/active-transformation))
