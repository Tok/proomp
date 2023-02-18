(ns proomp.domain.resolution
  (:import (clojure.lang MapEntry)))

(def ^:private orientations [::landscape ::portrait ::square])
(defprotocol Oriented (orientation [this]))

(defrecord Resolution [name w h aspect]
  Oriented (orientation [this] (cond (> w h) ::landscape (< w h) ::portrait :else ::square)))

(defn- swap-keyword [kw]
  "Swaps the orientation of resolution keywords. Eg. :16:9 into :9:16."
  (->> (-> (name kw) (clojure.string/split #":") reverse) (clojure.string/join ":") keyword))
(defn change-keyword-orientation [kw]
  (let [parts (clojure.string/split (name kw) #"-")]
    (if (= "landscape" (last parts))
      (keyword (apply str (interpose "-" (concat (butlast parts) ["portrait"]))))
      (keyword (apply str (interpose "-" (concat (butlast parts) ["landscape"])))))))

(defn- flip-orientation [^Resolution res]
  (->Resolution (:name res) (:h res) (:w res) (swap-keyword (:aspect res))))

(def ^:private square-resolutions
  {:highest (->Resolution "Highest" 1440 1440 :1:1)         ;requires more than 10GB VRAM
   :higher  (->Resolution "Higher" 1280 1280 :1:1)          ;may require more than 10GB VRAM
   :high    (->Resolution "High" 1080 1080 :1:1)
   :default (->Resolution "Default" 960 960 :1:1)
   :reduced (->Resolution "Reduced" 768 768 :1:1)
   :low     (->Resolution "Low" 720 720 :1:1)
   :lower   (->Resolution "Lower" 512 512 :1:1)             ;may work with 6GB VRAM
   :lowest  (->Resolution "Lowest" 360 360 :1:1)})          ;should work with 6GB VRAM

(def ^:private sixteen-to-nine-resolutions
  "See https://en.wikipedia.org/wiki/Graphics_display_resolution"
  {:4K-UHD-landscape  (->Resolution "4K UHD" 3840 2160 :16:9) ;requires more than 10GB VRAM
   :QHD-landscape     (->Resolution "QHD" 2560 1440 :16:9)  ;may require more than 10GB VRAM
   :Full-HD-landscape (->Resolution "Full HD" 1920 1080 :16:9) ;may not work with 10GB VRAM
   :HD-landscape      (->Resolution "HD" 1280 720 :16:9)
   :WSVGA-landscape   (->Resolution "WSVGA" 1024 576 :16:9)
   :NHD-landscape     (->Resolution "nHD" 640 360 :16:9)})  ;may work with 6GB VRAM

(def ^:private switch-orientation (fn [[k v]] (MapEntry. (change-keyword-orientation k) (flip-orientation v))))
(def resolutions
  (let [nine-to-sixteen-resolutions (into {} (map switch-orientation sixteen-to-nine-resolutions))]
    (merge square-resolutions sixteen-to-nine-resolutions nine-to-sixteen-resolutions)))
