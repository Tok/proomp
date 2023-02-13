(ns proomp.util.operation.rescale
  (:import (java.awt.image RescaleOp)
           (java.awt RenderingHints)))

(defn rescale [s1 s2 s3 o1 o2 o3]
  (let [scale-factors (float-array [s1 s2 s3])
        offsets (float-array [o1 o2 o3])
        hints (RenderingHints. {})]
    (RescaleOp. scale-factors offsets hints)))
