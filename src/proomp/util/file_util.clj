(ns proomp.util.file-util
  (:require [proomp.config :as config]))

(defonce image-format "png")
(defonce image-suffix (str "." image-format))

(defn prompt->file-name [prompt] (str config/media-path prompt image-suffix))
