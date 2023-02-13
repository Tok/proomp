(ns proomp.image-util-test
  (:require
    [proomp.config]
    [clojure.test :refer :all]
    [proomp.util.image-util :as image-util]))

(deftest new-py-image-test
  (let [pil-image (image-util/new-py-image 100 100)]
    (testing "Create PIL Image" (is (not (nil? pil-image))))))

(deftest pil->bi-test
  (let [pil-image (image-util/new-py-image 100 100)
        buffered-image (image-util/pil->bi pil-image)]
    (testing "PIL Image to Buffered Image" (is (not (nil? buffered-image))))))
