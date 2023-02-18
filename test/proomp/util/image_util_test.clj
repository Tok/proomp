(ns proomp.util.image-util-test
  (:require
    [proomp.config]
    [clojure.test :refer :all]
    [proomp.util.image-utils :as image-utils]))

(deftest new-py-image-test
  (let [pil-image (image-utils/new-py-image 100 100)]
    (testing "Create PIL Image" (is (not (nil? pil-image))))))

(deftest pil->bi-test
  (let [pil-image (image-utils/new-py-image 100 100)
        buffered-image (image-utils/pil->bi pil-image)]
    (testing "PIL Image to Buffered Image" (is (not (nil? buffered-image))))))
