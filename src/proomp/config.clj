(ns proomp.config
  (:require [libpython-clj2.python :as py]))

(println "Loading config.")

(def ^:private workspace-path "C:\\Users\\Zir\\Documents\\workspace\\proomp\\")
(def model-path (str workspace-path "models\\stable-diffusion-2-1\\"))
(def media-path (str workspace-path "generated-media\\"))

(def ^:private python-dir "\\Users\\Zir\\AppData\\Local\\Programs\\Python\\")
(py/initialize!
  :library-path (str python-dir "Python39\\python39.dll")
  :python-executable (str python-dir"Python39\\python.exe")
  :python-verbose false)
