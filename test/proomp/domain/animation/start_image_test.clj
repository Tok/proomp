(ns proomp.domain.animation.start-image-test
  (:require [proomp.domain.animation.start-image :refer :all]
            [proomp.test-utils :as test-utils]
            [clojure.test :refer :all]))

(deftest start-images-test
  (doseq [[k v] available-start-images]
    (test-utils/test-image-exists "Start Image" k v)))
