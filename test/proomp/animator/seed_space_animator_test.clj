(ns proomp.animator.seed-space-animator-test
  (:require [proomp.animator.seed-space-animator :refer :all]
            [proomp.util.image-utils :as image-utils]
            [clojure.test :refer :all]))

(deftest apply-transformations-test
(let [pil-image (image-utils/->pil-image 100 100)
      transformed (#'proomp.animator.seed-space-animator/apply-transformations pil-image)
      w-expected (image-utils/pil->w pil-image)
      h-expected (image-utils/pil->h pil-image)
      w-actual (image-utils/pil->w transformed)
      h-actual (image-utils/pil->h transformed)]
  (testing "Applying transformations preserves width" (is (= w-actual w-expected)))
  (testing "Applying transformations preserves height" (is (= h-actual h-expected)))))
