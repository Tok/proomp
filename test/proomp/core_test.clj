(ns proomp.core-test
  (:require [proomp.config]
            [proomp.core :refer :all]
            [clojure.test :refer :all]
            [libpython-clj2.require :refer [require-python]]))

(require-python '[torch :as torch])
(require-python '[torch.cuda :as cuda])

(deftest cuda-test (testing "Cuda" (is (cuda/is_available))))
(deftest torch-test
  (let [tensor (torch/rand 2 3)]
    (println tensor)
    (testing "Torch" (is (not (empty? tensor))))))
