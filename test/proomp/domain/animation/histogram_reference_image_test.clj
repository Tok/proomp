(ns proomp.domain.animation.histogram-reference-image-test
  (:require [proomp.domain.animation.histogram-reference-image :refer :all]
            [proomp.test-utils :as test-utils]
            [clojure.test :refer :all]))

(deftest histogram-reference-images-test
  (doseq [[k v] available-reference-images]
    (test-utils/test-image-exists "Reference Image" k v)))
