(ns proomp.constants)

(defonce w 768)                                             ;default 768
(defonce h 768)                                             ;default 768

(def scale 7.5)                                             ;default 7.5
(def iterations 50)                                         ;default 50

(def ^:private start-seed 0)
(def ^:private repetitions 100)
(def seed-range (range start-seed (+ start-seed repetitions)))

(def prompt-addition "((photorealistic)) (photo) focused professional")

(def neg-prompt-addition "(blurry) (drawing) comic bad grayscale deformed disfigured")
