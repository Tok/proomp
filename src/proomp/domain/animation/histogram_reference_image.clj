(ns proomp.domain.animation.histogram-reference-image)

(defrecord HistogramReferenceImage [local-file])

(def reference-images
  (let [dir "./images/histogram-references/"]
    {::none                nil
     ::default-black-white (->HistogramReferenceImage (str dir "default-black-white.png"))}))
(def available-reference-images (filter #(not (= (key %) ::none)) reference-images))

(def active-reference-image (::none reference-images))
