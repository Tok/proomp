(ns proomp.config
  (:require [cambium.core :as log]
            [libpython-clj2.python :as py]))

(log/info "Loading config.")
(def ^:private python-home "C:\\Users\\Zir\\AppData\\Local\\Programs\\Python\\Python311\\")
(log/info {:python-home python-home})

(defonce ^:private workspace-path "C:\\Users\\Zir\\Documents\\workspace\\proomp\\")
(defonce model-path (str workspace-path "models\\stable-diffusion-2-1\\"))
(log/debug {:model-path model-path})

(defonce upscaler-model-path (str workspace-path "models\\stable-diffusion-x4-upscaler\\"))
(log/debug {:upscaler-model-path upscaler-model-path})

(defonce riffusion-model-path (str workspace-path "models\\riffusion-model-v1\\"))
(log/debug {:riffusion-model-path riffusion-model-path})
(defonce riffusion-path (str workspace-path "riffusion\\"))
(log/debug {:riffusion-path riffusion-path})

(defonce media-path (str workspace-path "generated-media\\"))
(log/debug {:media-path media-path})

(defonce temp-media-path (str media-path "temp\\"))
(log/debug {:temp-media-path temp-media-path})

(defonce image-path (str workspace-path "images\\"))
(log/debug {:image-path image-path})

(py/initialize!
  :python-home python-home
  :python-executable (str python-home "python.exe")
  :library-path (str python-home "python311.dll")
  :python-verbose true)

(py/import-module "riffusion")
