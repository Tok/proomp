(ns proomp.util.pipe-util
  (:require [proomp.config :as config]
            [proomp.constants :as const]
            [cambium.core :as log]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.- py*] :as py]))

(require-python 'torch '[torch.cuda :as cuda] 'transformers)
(require-python '[diffusers :refer [StableDiffusionPipeline StableDiffusionImg2ImgPipeline]])

(defonce device "cuda")

(defn- clear-cuda-cache []
  (cuda/empty_cache)
  (log/trace "Cuda cache cleared."))

(defn- send-to-device [pipe] (py. pipe "to" device))
(defn- enable-attention-slicing [pipe]
  (py. pipe "enable_attention_slicing")
  (log/debug "Attention slicing enabled."))

(defn- ->pipeline [type model-path]
  (log/debug "Creating Pipeline.")
  (clear-cuda-cache)
  (let [pipe (py. type "from_pretrained" model-path
                  :torch_dtype torch/float16)]
    (send-to-device pipe)
    (enable-attention-slicing pipe)
    (log/trace {:pipe pipe})
    pipe))

(defn ->text-to-image-pipeline [] (->pipeline StableDiffusionPipeline config/model-path))
(defn ->image-to-image-pipeline [] (->pipeline StableDiffusionImg2ImgPipeline config/model-path))
;FIXME make i2i work with newer models

(defn- ->generator [seed] (py. (py/$c torch/Generator device) "manual_seed" seed))

(defn- extract-first-image [result] (nth (py.- result :images) 0))
(defn generate-image [pipe prompt neg-prompt seed]
  (extract-first-image
    (py/$c pipe (str prompt const/prompt-addition)
           :negative_prompt (str neg-prompt const/neg-prompt-addition)
           :generator (->generator seed)
           :height const/h :width const/w
           :num_inference_steps const/iterations
           :guidance_scale const/scale)))

(defn generate-i2i [pipe prompt neg-prompt seed init-image]
  (extract-first-image
    (py/$c pipe (str prompt const/prompt-addition)
           :negative_prompt (str neg-prompt const/neg-prompt-addition)
           :init_image init-image
           :strength const/ani-noise
           :generator (->generator seed)
           :num_inference_steps const/ani-iterations
           :guidance_scale const/ani-scale)))
