(ns proomp.domain.animation.frame-transformation-test
  (:require
    [proomp.domain.animation.frame-transformation :refer :all]
    [proomp.util.image-utils :as image-utils]
    [clojure.test :refer :all]))

(deftest apply-transformations-test
  (let [t (->FrameTransformation 1.111 -1.1 -1 -1 true)
        pil-image (image-utils/->pil-image 100 100)
        transformed (image-utils/apply-transformations pil-image t)
        w-expected (image-utils/pil->w pil-image)
        h-expected (image-utils/pil->h pil-image)
        w-actual (image-utils/pil->w transformed)
        h-actual (image-utils/pil->h transformed)]
    (testing "Applying transformations preserves width" (is (= w-actual w-expected)))
    (testing "Applying transformations preserves height" (is (= h-actual h-expected)))))
