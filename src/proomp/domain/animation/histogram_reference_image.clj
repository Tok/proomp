(ns proomp.domain.animation.histogram-reference-image)

(defrecord HistogramReferenceImage [local-file])

(def reference-images
  (let [dir "./images/histogram-references/"]
    {:none                nil
     :default-black-white (->HistogramReferenceImage (str dir "default-black-white.png"))}))
