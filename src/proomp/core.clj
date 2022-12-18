(ns proomp.core
  (:require [proomp.config :as config]
            [proomp.constants :as const]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.-] :as py]))
(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[diffusers :refer [StableDiffusionPipeline DPMSolverMultistepScheduler]])

(defn -main []
  (cuda/empty_cache)
  (println "Creating pipeline.")
  (let [file-name (str config/media-path const/prompt ".png")
        pipe (py. StableDiffusionPipeline "from_pretrained" config/model-path)
        pipe-scheduler (py.- pipe "scheduler")
        pipe-scheduler-config (py.- pipe-scheduler "config")
        dpm-scheduler (py. DPMSolverMultistepScheduler "from_config" pipe-scheduler-config)]
    (py/set-attr! pipe "scheduler" dpm-scheduler)
    (py. pipe "to" "cuda")
    (println "Enabling attention slicing.")
    (py. pipe "enable_attention_slicing")
    (println "Generating" file-name)
    (let [result (py/$c pipe const/prompt)
          images (py.- result "images")
          image (nth images 0)]
      (py. image "save" file-name)
      (newline)
      (println "Done."))))
