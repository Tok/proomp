(ns proomp.domain.animation.frame-transformation)

;;Transformations are applied between generating animation frames.
(defrecord FrameTransformation
  [zoom rotation-degree x-offset-pixels y-offset-pixels color-correction?])

(def ^:private transformation-templates
  ;speed settings depend on the intended FPS.
  {::none                       {:zoom                    1.0
                                 :rotation-degree         0.0
                                 :x-offset-pixels         0
                                 :y-offset-pixels         0
                                 :apply-color-correction? false}
   ::color-corrected            {:color-correction? true}
   ::slowest-zoom               {:zoom 1.005}
   ::very-slow-zoom             {:zoom 1.010}
   ::slow-zoom                  {:zoom 1.020}
   ::default-zoom               {:zoom 1.050}
   ::fast-zoom                  {:zoom 1.100}
   ::clockwise-rotation         {:rotation-degree -0.5}
   ::counter-clockwise-rotation {:rotation-degree 0.5}
   ::move-left-slow             {:x-offset-pixels -5}
   ::move-right-slow            {:x-offset-pixels 5}
   ::move-up-slow               {:y-offset-pixels -5}
   ::move-down-slow             {:y-offset-pixels 5}
   ::move-left                  {:x-offset-pixels -10}
   ::move-right                 {:x-offset-pixels 10}
   ::move-up                    {:y-offset-pixels -10}
   ::move-down                  {:y-offset-pixels 10}})

(defn active-transformation []
  (map->FrameTransformation
    (merge
      (::none transformation-templates)                     ;leave ::none as 1st argument
      (::color-corrected transformation-templates)
      (::very-slow-zoom transformation-templates))))

(defn apply-transformations? [trans] (not (= trans (map->FrameTransformation (::none transformation-templates)))))
