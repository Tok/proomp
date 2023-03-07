(ns proomp.tools.riffusion-client
  (:require
    [cambium.core :as log]
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.java.io :refer [file output-stream]]
    [clojure.string :as str]
    [org.httpkit.client :as http]
    [libpython-clj2.require :refer [require-python]]
    [proomp.util.file-utils :as file-utils])
  (:import (proomp.domain.prompt.prompt Prompt))
  (:import java.util.Base64))

(require-python 'os)

(defonce riffusion-host "127.0.0.1")
(defonce riffusion-port 3013)

(defonce duration-s 180)
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
  {:prompt          (:text prompt)
   :negative_prompt (:negative-text prompt)
   :seed            seed
   :denoising       denoising
   :guidance        guidance})

(defn- ->riffusion-request [prompt seed alpha]
  {:start               (->riffusion-prompt prompt seed)
   :end                 (->riffusion-prompt prompt (inc seed))
   :alpha               alpha
   :num_inference_steps num-inference-steps
   :seed_image_id       seed-image-id})

(defn- base64->bin [base64] (.decode (Base64/getDecoder) base64))
(defn- base64->file! [target-file-name base64]
  (let [bytes (base64->bin base64)
        target-file (file target-file-name)]
    (io/make-parents target-file-name)
    (log/info (str "Saving: " target-file))
    (with-open [out (output-stream target-file)]
      (.write out bytes))))

(defn- extract-base64 [data]
  (str/replace (last (str/split data #",")) #"\n" ""))
(defn- data->mp3! [prompt-text data step]
  (let [file-name (file-utils/audio-name prompt-text step)]
    (base64->file! file-name (extract-base64 data))))
(defn- data->image! [prompt-text data step]
  (let [file-name (file-utils/spectrogram-name prompt-text step)]
    (base64->file! file-name (extract-base64 data))))

(defn post-riffusion-request [^Prompt prompt seed]
  (log/info {:denoising denoising})
  (log/info {:guidance guidance})
  (log/info {:num-inference-steps num-inference-steps})
  (log/info {:seed-image-id seed-image-id})
  (let [step-s 5
        num-steps (inc (int (/ duration-s step-s)))]
    (doseq [step (range num-steps)]
      (let [alpha (/ step num-steps)
            request {:body (json/write-str (->riffusion-request prompt seed alpha))}]
        (log/debug {:request request})
        (http/post
          "http://127.0.0.1:3013/run_inference/"
          request
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
                  (data->image! (:text prompt) image-data step)
                  (data->mp3! (:text prompt) audio-data step)))
              ;If there's not enough memory available,
              ;Riffusion may respond with an error-xml:
              ;"JSON error (unexpected character): <"
              (catch Exception e (log/error {:e e})))
            (if (= (inc step) num-steps) (System/exit 0))))))))
