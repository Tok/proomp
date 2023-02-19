(ns proomp.domain.animation.frame-transformation
  (:require [cambium.core :as log])
  (:import (clojure.lang MapEntry)))

;;Transformations are applied between generating animation frames.
(defrecord FrameTransformation
  [zoom rotation-degree x-offset-pixels y-offset-pixels apply-color-correction?])

(def ^:private transformation-templates
  {::none                       (->FrameTransformation 1.0 0.0 0 0 false)
   ::color-corrected            (map->FrameTransformation {:apply-color-correction? true})
   ::slowest-zoom               (map->FrameTransformation {:zoom 1.005})
   ::very-slow-zoom             (map->FrameTransformation {:zoom 1.010})
   ::slow-zoom                  (map->FrameTransformation {:zoom 1.020})
   ::default-zoom               (map->FrameTransformation {:zoom 1.050})
   ::fast-zoom                  (map->FrameTransformation {:zoom 1.100})
   ::clockwise-rotation         (map->FrameTransformation {:rotation-degree -0.5})
   ::counter-clockwise-rotation (map->FrameTransformation {:rotation-degree 0.5})
   ::move-left-slow             (map->FrameTransformation {:x-offset-pixels -5})
   ::move-right-slow            (map->FrameTransformation {:x-offset-pixels 5})
   ::move-up-slow               (map->FrameTransformation {:y-offset-pixels -5})
   ::move-down-slow             (map->FrameTransformation {:y-offset-pixels 5})
   ::move-left                  (map->FrameTransformation {:x-offset-pixels -10})
   ::move-right                 (map->FrameTransformation {:x-offset-pixels 10})
   ::move-up                    (map->FrameTransformation {:y-offset-pixels -10})
   ::move-down                  (map->FrameTransformation {:y-offset-pixels 10})})

(defn- merge-with-default [entry]
  (let [[k v] entry
        zoom (min (or (:zoom v) 1.000) 1.000)
        rd (:rotation-degree v)
        deg (if (not (nil? rd)) rd 0.0)
        rot (mod deg 360.0)
        x-off (or (:x-offset-pixels v) 0)
        y-off (or (:y-offset-pixels v) 0)
        correct? (if (:apply-color-correction? v) true false)]
    (MapEntry. k (->FrameTransformation zoom rot x-off y-off correct?))))

(def transformations (into {} (map merge-with-default transformation-templates)))
(defn active-transformation [] (::move-left transformations))

(defn apply-transformations? [trans]
  (not (= trans (::none transformations))))
