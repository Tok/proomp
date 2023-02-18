(ns proomp.domain.image.image)

(defrecord Image [^ImageFormat image-format ^Resolution resolution]
  Oriented (orientation [this] (orientation resolution)))
