(ns proomp.util.pipe-util
  (:require [proomp.config :as config]
            [cambium.core :as log]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.-] :as py]))

(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[diffusers :refer [StableDiffusionPipeline DPMSolverMultistepScheduler]])

(defonce device "cuda")

(defn create-pipeline []
  (log/debug "Creating pipeline.")
  (cuda/empty_cache)
  (log/debug "Cuda cache cleared.")
  (let [pipe (py. StableDiffusionPipeline "from_pretrained" config/model-path)
        scheduler (py.- pipe "scheduler")
        scheduler-config (py.- scheduler "config")
        dpm-scheduler (py. DPMSolverMultistepScheduler "from_config" scheduler-config)]
    (py/set-attr! pipe "scheduler" dpm-scheduler)
    (log/trace {:scheduler dpm-scheduler})
    (py. pipe "to" device)
    (log/debug {:device device})
    (py. pipe "enable_attention_slicing")
    (log/debug "Attention slicing enabled.")
    pipe))

(defn- extract-first-image [result] (nth (py.- result "images") 0))
(defn generate-image [pipe prompt] (->> prompt (py/$c pipe) extract-first-image))
