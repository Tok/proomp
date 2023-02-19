(defproject proomp "0.7.0-SNAPSHOT"
  :description "A Clojure workspace for Stable Diffusion"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clojure-interop/javax.imageio "1.0.5"]
                 [cambium/cambium.core "1.1.1"]
                 [cambium/cambium.codec-simple "1.0.0"]
                 [cambium/cambium.logback.core "0.4.5"]
                 [clj-python/libpython-clj "2.021"]
                 [org.jcodec/jcodec "0.2.5"]
                 [org.jcodec/jcodec-javase "0.2.5"]]
  :plugins [[lein-cloverage "1.2.4"]]
  :jvm-opts ["--add-modules" "jdk.incubator.foreign,jdk.incubator.vector"
             "--enable-native-access=ALL-UNNAMED"]
  :repl-options {:init-ns proomp.config}
  :aot [proomp.core]
  :main proomp.core)
