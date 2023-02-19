(ns proomp.util.file-util-test
  (:require
    [clojure.string :as str]
    [clojure.test :refer :all]
    [proomp.util.file-utils :refer :all]))

(deftest file-name-test
  (let [name (file-name "foo" 0)]
    (testing "Empty Prompt Seed Space Animation"
      (is (= "0000_foo.png" (last (str/split name #"\\")))))))
