(ns proomp.domain.animation.start-image)

(defrecord StartImage [orientation local-file])

(def start-images
  (let [start-images-dir "./images/start-images/"
        landscape-dir (str start-images-dir "landscape/")
        portrait-dir (str start-images-dir "portrait/")]
    {::none                nil
     ::mandelbrot-set-blue (->StartImage :landscape (str landscape-dir "mandelbrot-set-blue.png"))
     ::the-magician        (->StartImage :portrait (str portrait-dir "rider-waite-tarot-card-the-magician.png"))}))
(def available-start-images (filter #(not (= (key %) ::none)) start-images))

(def active-start-image (::none start-images))
