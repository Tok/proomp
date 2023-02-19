(ns proomp.domain.animation.transformation-test
  (:require [proomp.domain.animation.transformation :refer :all]
            [clojure.test :refer :all]))

(deftest transformations-setup-test
  (doseq [[k v] transformations]
    (testing (str "Transformation " k " has valid zoom: " (:zoom v))
      (is (>= (:zoom v) 1.000)))))
