(ns proomp.core
  (:require [proomp.config :as config]
            [proomp.constants :as const]
            [cambium.core :as log]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.-] :as py]))
(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[diffusers :refer [StableDiffusionPipeline DPMSolverMultistepScheduler]])

(def prompt "Caliper Remote")

(defn -main []
  (cuda/empty_cache)
  (log/debug "Creating pipeline.")
  (let [file-name (str config/media-path prompt ".png")
        pipe (py. StableDiffusionPipeline "from_pretrained" config/model-path)
        pipe-scheduler (py.- pipe "scheduler")
        pipe-scheduler-config (py.- pipe-scheduler "config")
        dpm-scheduler (py. DPMSolverMultistepScheduler "from_config" pipe-scheduler-config)]
    (py/set-attr! pipe "scheduler" dpm-scheduler)
    (py. pipe "to" "cuda")
    (py. pipe "enable_attention_slicing")

    (log/info {:prompt prompt})
    (log/debug {:file-name file-name})
    (let [result (py/$c pipe prompt)
          images (py.- result "images")
          image (nth images 0)]
      (py. image "save" file-name))))
