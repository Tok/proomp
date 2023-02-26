(ns proomp.domain.pipe.pipe-setup)

(defrecord PipeSetup [iterations scale noise])
(defn k->iters [k]
  "Choice of quality vs. speed."
  (condp = k :faster 10 :fast 20 :default 30 :quality 40 :high-quality 50 :else 40))
(defn k->noise [k]
  "Affects the speed of change between frames."
  (condp = k :calm 0.50 :default 0.55 :noisy 0.60 :chaotic 0.65 :else 0.55))
(defn k->scale [k]
  "The Animation Scale."
  (condp = k :low 7.0 :default 7.5 :high 8.0 :else 7.5))


(def image-pipes
  {:fast    (->PipeSetup (k->iters :fast) (k->scale :default) nil)
   :default (->PipeSetup (k->iters :default) (k->scale :default) nil)
   :quality (->PipeSetup (k->iters :quality) (k->scale :default) nil)})
(def i2i-pipes
  {:faster        (->PipeSetup (k->iters :faster) (k->scale :default) (k->noise :default))
   :fast          (->PipeSetup (k->iters :fast) (k->scale :default) (k->noise :default))
   :default       (->PipeSetup (k->iters :default) (k->scale :default) (k->noise :default))
   :quality       (->PipeSetup (k->iters :quality) (k->scale :default) (k->noise :default))
   :fastest-calm  (->PipeSetup (k->iters :faster) (k->scale :default) (k->noise :calm))
   :fast-calm     (->PipeSetup (k->iters :fast) (k->scale :default) (k->noise :calm))
   :default-calm  (->PipeSetup (k->iters :default) (k->scale :default) (k->noise :calm))
   :quality-calm  (->PipeSetup (k->iters :quality) (k->scale :default) (k->noise :calm))
   :fastest-noisy (->PipeSetup (k->iters :faster) (k->scale :default) (k->noise :noisy))
   :fast-noisy    (->PipeSetup (k->iters :fast) (k->scale :default) (k->noise :noisy))
   :default-noisy (->PipeSetup (k->iters :default) (k->scale :default) (k->noise :noisy))
   :quality-noisy (->PipeSetup (k->iters :quality) (k->scale :default) (k->noise :noisy))})

(defonce image-pipe-setup (:default image-pipes))
(defonce i2i-pipe-setup (:fast-noisy i2i-pipes))
