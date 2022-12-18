(ns proomp.util.pipe-util
  (:require [proomp.config :as config]
            [proomp.constants :as const]
            [cambium.core :as log]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.-] :as py]))

(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[diffusers :refer [StableDiffusionPipeline StableDiffusionImg2ImgPipeline]])

(defonce device "cuda")

(defn- clear-cuda-cache []
  (cuda/empty_cache)
  (log/debug "Cuda cache cleared."))
(defn- send-to-device [pipe]
  (py. pipe "to" device)
  (log/debug {:device device}))
(defn- enable-attention-slicing [pipe]
  "Save VRAM at the cost of performance."
  (py. pipe "enable_attention_slicing")
  (log/debug "Attention slicing enabled."))

(defn- ->pipeline [type]
  (log/debug (str "Creating pipeline:" type))
  (clear-cuda-cache)
  (let [pipe (py. type "from_pretrained" config/model-path :torch_dtype torch/float16)]
    (send-to-device pipe)
    (enable-attention-slicing pipe)
    (log/trace {:pipe pipe})
    pipe))

(defn ->text-to-image-pipeline [] (->pipeline StableDiffusionPipeline))
(defn ->image-to-image-pipeline [] (->pipeline StableDiffusionImg2ImgPipeline))



(defn- extract-first-image [result] (nth (py.- result :images) 0))
(defn generate-image [pipe prompt seed]
  (let [generator (py. (py/$c torch/Generator device) "manual_seed" seed)
        result (py/$c pipe prompt
                      :generator generator
                      :height const/h :width const/w
                      :num_inference_steps const/iterations
                      :guidance_scale const/scale)]
    (extract-first-image result)))
