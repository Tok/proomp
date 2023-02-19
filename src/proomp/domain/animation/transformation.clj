(ns proomp.domain.animation.transformation
  (:import (clojure.lang MapEntry)))

(defrecord Transformation [zoom rotation-degree x-offset-pixels y-offset-pixels])
(def ^:private transformation-templates
  {:none                       (->Transformation 1.0 0.0 0 0)
   :slow-zoom                  (map->Transformation {:zoom 1.020})
   :default-zoom               (map->Transformation {:zoom 1.050})
   :fast-zoom                  (map->Transformation {:zoom 1.100})
   :clockwise-rotation         (map->Transformation {:rotation-degree -1.0})
   :counter-clockwise-rotation (map->Transformation {:rotation-degree 1.0})
   :move-left                  (map->Transformation {:x-offset-pixels -5})
   :move-right                 (map->Transformation {:x-offset-pixels 5})
   :move-up                    (map->Transformation {:y-offset-pixels -5})
   :move-down                  (map->Transformation {:y-offset-pixels 5})})

(defn- merge-with-default [entry]
  (let [[k v] entry
        zoom (min (or (:zoom v) 1.000) 1.000)
        rot (mod (or (:rotation-degree v) 0.0) 360.0)
        x-off (or (:x-offset-pixels v) 0)
        y-off (or (:y-offset-pixels v) 0)]
    (MapEntry. k (->Transformation zoom rot x-off y-off))))

(defonce transformations (map merge-with-default transformation-templates))
