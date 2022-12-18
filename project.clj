(defproject proomp "0.1.0-SNAPSHOT"
  :description "A Clojure workspace for Stable Diffusion"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [cambium/cambium.core "1.1.1"]
                 [cambium/cambium.codec-simple "1.0.0"]
                 [cambium/cambium.logback.core "0.4.5"]
                 [clj-python/libpython-clj "2.021"]]
  :jvm-opts ["--add-modules" "jdk.incubator.foreign,jdk.incubator.vector"
             "--enable-native-access=ALL-UNNAMED"]
  :repl-options {:init-ns proomp.config}
  :aot [proomp.core]
  :main proomp.core)
