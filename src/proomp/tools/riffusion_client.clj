(ns proomp.tools.riffusion-client
  (:require
    [cambium.core :as log]
    [clojure.data.json :as json]
    [org.httpkit.client :as http]
    [libpython-clj2.python :as py :refer [py. py.. py.-]]
    [libpython-clj2.require :refer [require-python] :as require-python]
    [proomp.domain.prompt.prompt :as prom])
  (:import (proomp.domain.prompt.prompt Prompt)))

(require-python 'os)

(defonce riffusion-host "127.0.0.1")
(defonce riffusion-port 3013)

(defn start-streamlit []
  (os/chdir "riffusion")
  (let [host "127.0.0.1"
        port 8501
        module "streamlit"
        target "riffusion/streamlit/playground.py"
        args ["python -m" module "run" target
              "--browser.serverAddress" host
              "--browser.serverPort" port]
        command (clojure.string/join " " args)]
    (future (os/system command))))

(defn start-riffusion-server []
  (os/chdir "riffusion")
  (let [module "riffusion.server"
        args ["python -m" module
              "--host" riffusion-host
              "--port" riffusion-port]
        command (clojure.string/join " " args)]
    (future (os/system command))))

(defn cli-h []
  (os/chdir "riffusion")
  (os/system "python -m riffusion.cli -h"))

(defonce denoising 0.75)
(defonce guidance 7.0)
(defonce num-inference-steps 50)
(defonce seed-image-id "og_beat")

(defn- ->riffusion-prompt [prompt seed]
  {:prompt          (prom/full-prompt prompt)
   :negative_prompt (prom/full-negative-prompt prompt)
   :seed            seed
   :denoising       denoising
   :guidance        guidance})

(defn- ->riffusion-request [prompt seed]
  {:start               (->riffusion-prompt prompt seed)
   :end                 (->riffusion-prompt prompt (inc seed))
   :alpha               0.5                                 ;TODO map range from 0.0 to 1.0
   :num_inference_steps num-inference-steps
   :seed_image_id       seed-image-id})

(comment defn post-riffusion-request [^Prompt prompt seed]
  (let [request-body (->riffusion-request prompt seed)
        api-url "http://127.0.0.1:3013/run_inference/"]
    (log/info {:request-body request-body})
    (try-request request-body api-url)))

(defn post-riffusion-request [^Prompt prompt seed]
  (log/info {:body (json/write-str (->riffusion-request prompt seed))})
  (http/post
    "http://127.0.0.1:3013/run_inference/"
    {:body (json/write-str (->riffusion-request prompt seed))}
    (fn [{:keys [status headers body error opts]}]
      (if error
        (log/error (str "Fail: " error))
        (let [response (json/read-str body)
              image (get response "image")
              audio (get response "audio")
              duration-s (get response "duration_s")]
          (log/info {:status status :duration-s duration-s})
          (log/info {:image image})                         ;TODO
          (log/info {:audio audio}))))))                    ;TODO
