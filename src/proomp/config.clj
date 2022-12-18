(ns proomp.config
  (:require [proomp.constants :as const]
            [cambium.core :as log]
            [libpython-clj2.python :as py]))

(log/info "Loading config.")


(def ^:private python-dir "\\Users\\Zir\\AppData\\Local\\Programs\\Python\\")
(log/trace {:python-dir python-dir})

(def ^:private workspace-path "C:\\Users\\Zir\\Documents\\workspace\\proomp\\")
(def model-path (str workspace-path "models\\stable-diffusion-2-1\\"))
(def media-path (str workspace-path "generated-media\\"))
(log/debug {:model-path model-path})
(log/debug {:media-path media-path})

(log/info {:width const/w :height const/h :iterations const/iterations :scale const/scale})

(py/initialize!
  :library-path (str python-dir "Python39\\python39.dll")
  :python-executable (str python-dir "Python39\\python.exe")
  :python-verbose false)
