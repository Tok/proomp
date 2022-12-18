(defproject proomp "0.1.0-SNAPSHOT"
  :description "A Clojure workspace for Stable Diffusion"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clj-python/libpython-clj "2.021"]]
  :repl-options {:init-ns proomp.config})
