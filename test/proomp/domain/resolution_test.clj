(ns proomp.domain.resolution-test
  (:require [proomp.domain.resolution :refer :all]
            [clojure.test :refer :all]))

(deftest swap-keyword-test
  (testing "Swap aspect keyword"
    (is (= (#'proomp.domain.resolution/swap-keyword :16:9) :9:16)))
  (testing "Preserve 1:1 aspect"
    (is (= (#'proomp.domain.resolution/swap-keyword :1:1) :1:1))))

(deftest divisible-by-8-test
  (doseq [[k v] resolutions]
    (testing (str "Width is required to be evenly divisible by 8 for Stable Diffusion." k)
      (is (= (mod (:w v) 8) 0)))
    (testing (str "Height is required to be evenly divisible by 8 for Stable Diffusion." k)
      (is (= (mod (:h v) 8) 0)))))

(deftest orientation-flip-test
  (let [landscape (:Full-HD-landscape resolutions)
        portrait (:Full-HD-portrait resolutions)]
    (testing "Orientation switch is correct."
      (is (and (= (:w landscape) (:h portrait))
               (= (:h landscape) (:w portrait)))))))
