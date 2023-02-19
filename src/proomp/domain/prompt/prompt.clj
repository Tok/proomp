(ns proomp.domain.prompt.prompt)

(defrecord Prompt [text negative-text additions negative-additions])

(defn full-prompt [^Prompt p] (str (:text p) " " (:additions p)))
(defn full-negative-prompt [^Prompt p] (str (:negative-text p) " " (:negative-additions p)))
