(ns proomp.domain.image)

(defrecord Image [^ImageFormat image-format ^Resolution resolution]
  Oriented (orientation [this] (orientation resolution)))
