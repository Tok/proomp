(ns proomp.domain.animation.start-image-test
  (:require [proomp.domain.animation.start-image :refer :all]
            [proomp.util.image-utils :as image-utils]
            [clojure.test :refer :all]))

(deftest start-images-test
  (doseq [[k v] (filter #(not (= (key %) :none)) start-images)]
    (testing (str "Start image " k " is available.")
      (is (not (nil? v))))
    (let [start-image (image-utils/open-py-image (:local-file v))]
      (testing (str "Start image " k " is a readable image.")
        (is (not (nil? start-image))))
      (testing (str "Start image " k " has a width > 0.")
        (is (> (image-utils/pil->w start-image) 0)))
      (testing (str "Start image " k " has a height > 0.")
        (is (> (image-utils/pil->h start-image) 0))))))
