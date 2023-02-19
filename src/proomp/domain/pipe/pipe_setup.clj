(ns proomp.domain.pipe.pipe-setup)

(defrecord PipeSetup [iterations scale noise])              ;default 40 7.5 0.55

(defonce image-pipe-setup (->PipeSetup 40 7.5 nil))
(defonce i2i-pipe-setup (->PipeSetup 30 7.5 0.55))
