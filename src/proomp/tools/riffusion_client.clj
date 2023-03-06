(ns proomp.tools.riffusion-client
  (:require
    [cambium.core :as log]
    [clojure.data.json :as json]
    [clojure.java.io :refer [file output-stream]]
    [clojure.string :as str]
    [org.httpkit.client :as http]
    [libpython-clj2.require :refer [require-python]]
    [proomp.domain.prompt.prompt :as prom])
  (:import (proomp.domain.prompt.prompt Prompt))
  (:import java.util.Base64))

(require-python 'os)

(defonce riffusion-host "127.0.0.1")
(defonce riffusion-port 3013)

(defonce denoising 0.75)
(defonce guidance 7.0)
(defonce num-inference-steps 50)
(defonce seed-image-id "og_beat")

(defn start-riffusion-server []
  (os/chdir "riffusion")
  (let [module "riffusion.server"
        args ["python -m" module
              "--host" riffusion-host
              "--port" riffusion-port]
        command (clojure.string/join " " args)]
    (future (os/system command))))

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

(defn- base64->bin [base64] (.decode (Base64/getDecoder) base64))
(defn- base64->file! [target-file base64]
  (let [bytes (base64->bin base64)]
    (log/info (str "Saving: " target-file))
    (with-open [out (output-stream target-file)]
      (.write out bytes))))

(def ^:private audio-out "../generated-media/riffusion.mp3")
(def ^:private image-out "../generated-media/riffusion.png")
(defn- extract-base64 [data]
  (str/replace (last (str/split data #",")) #"\n" ""))
(defn- data->mp3! [data]
  (base64->file! (file audio-out) (extract-base64 data)))
(defn- data->image! [data]
  (base64->file! (file image-out) (extract-base64 data)))

(defn post-riffusion-request [^Prompt prompt seed]
  (log/debug {:body (json/write-str (->riffusion-request prompt seed))})
  (log/info {:denoising denoising})
  (log/info {:guidance guidance})
  (log/info {:num-inference-steps num-inference-steps})
  (log/info {:seed-image-id seed-image-id})
  (http/post
    "http://127.0.0.1:3013/run_inference/"
    {:body (json/write-str (->riffusion-request prompt seed))}
    (fn [{:keys [status headers body error opts]}]
      (try
        (if error
          (log/error (str "Fail: " error))
          (let [response (json/read-str body)
                image-data (get response "image")
                audio-data (get response "audio")
                duration-s (get response "duration_s")]
            (log/info {:status status :duration-s duration-s})
            (log/debug {:image-data image-data})
            (log/debug {:audio-data audio-data})
            (data->image! image-data)
            (data->mp3! audio-data)
            (System/exit 0)))
        ;If there's not enough memory available,
        ;Riffusion may respond with an error-xml:
        ;"JSON error (unexpected character): <"
        (catch Exception e (log/error {:e e}))))))
