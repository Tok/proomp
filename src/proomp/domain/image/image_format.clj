(ns proomp.domain.image.image-format)

(defrecord ImageFormat [file-suffix])

(def image-formats {:PNG (->ImageFormat ".png") :JPG (->ImageFormat ".jpg")})
