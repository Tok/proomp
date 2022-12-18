(ns proomp.util.pipe-util
  (:require [proomp.config :as config]
            [proomp.constants :as const]
            [cambium.core :as log]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.-] :as py]))

(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[diffusers :refer [StableDiffusionPipeline]])

(defonce device "cuda")

(defn create-pipeline []
  (log/debug "Creating pipeline.")
  (cuda/empty_cache)
  (log/debug "Cuda cache cleared.")
  (let [pipe (py. StableDiffusionPipeline "from_pretrained" config/model-path
                  :torch_dtype torch/float16)]
    (py. pipe "to" device)
    (log/debug {:device device})
    (py. pipe "enable_attention_slicing")
    (log/debug "Attention slicing enabled.")
    pipe))

(defn- extract-first-image [result] (nth (py.- result "images") 0))
(defn generate-image [pipe prompt seed]
  (let [generator (py. (py/$c torch/Generator device) "manual_seed" seed)
        result (py/$c pipe prompt
                      :generator generator
                      :height const/h :width const/w
                      :num_inference_steps const/iterations
                      :guidance_scale const/scale)]
    (extract-first-image result)))
