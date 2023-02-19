(ns proomp.util.image-util-test
  (:require
    [proomp.config]
    [clojure.test :refer :all]
    [proomp.util.image-utils :refer :all]))

(deftest new-py-image-test
  (let [pil-image (->pil-image 100 100)]
    (testing "Create PIL Image" (is (not (nil? pil-image))))))

(deftest new-buffered-image-test
  (let [buffered-image (->image 100 100)]
    (testing "Create BufferedImage" (is (not (nil? buffered-image))))))

(deftest pil->bi-test
  (let [pil-image (->pil-image 100 100)
        buffered-image (pil->bi pil-image)]
    (testing "PIL Image to BufferedImage" (is (not (nil? buffered-image))))))

(deftest pil->numpy-test
  (let [pil-image (->pil-image 100 100)
        numpy-image (pil->numpy pil-image)]
    (testing "PIL Image to Numpy image" (is (not (nil? numpy-image))))))

(deftest numpy->pil-test
  (let [pil-image (->pil-image 100 100)
        numpy-image (pil->numpy pil-image)
        pil-again (numpy->pil numpy-image)
        w-expected (pil->w pil-image)
        h-expected (pil->h pil-image)
        w-actual (pil->w pil-again)
        h-actual (pil->h pil-again)]
    (testing "Numpy to PIL image" (is (not (nil? numpy-image))))
    (testing "Numpy to PIL image preserves width" (is (= w-actual w-expected)))
    (testing "Numpy to PIL image preserves height" (is (= h-actual h-expected)))))

(deftest zoom-in-test
  (let [pil-image (->pil-image 100 100)
        zoomed (zoom-center pil-image 2.000)
        w-expected (pil->w pil-image)
        h-expected (pil->h pil-image)
        w-actual (pil->w zoomed)
        h-actual (pil->h zoomed)]
    (testing "Zoom preserves width" (is (= w-actual w-expected)))
    (testing "Zoom preserves height" (is (= h-actual h-expected)))))
