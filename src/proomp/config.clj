(ns proomp.config
  (:require [cambium.core :as log]
            [libpython-clj2.python :as py]))

(log/info "Loading config.")
(def ^:private python-dir "C:\\Users\\Zir\\AppData\\Local\\Programs\\Python\\")
(log/trace {:python-dir python-dir})

(defonce ^:private workspace-path "C:\\Users\\Zir\\Documents\\workspace\\proomp\\")
(defonce model-path (str workspace-path "models\\stable-diffusion-2-1\\"))
(log/debug {:model-path model-path})

(defonce media-path (str workspace-path "generated-media\\"))
(log/debug {:media-path media-path})

(defonce temp-media-path (str media-path "temp\\"))
(log/debug {:temp-media-path temp-media-path})

(defonce image-path (str workspace-path "images\\"))
(log/debug {:image-path image-path})

(py/initialize!
  :library-path (str python-dir "Python39\\python39.dll")
  :python-executable (str python-dir "Python39\\python.exe")
  :python-verbose true)
