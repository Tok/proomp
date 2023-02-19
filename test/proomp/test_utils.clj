(ns proomp.test-utils
  (:require [clojure.test :refer :all]
            [proomp.util.image-utils :as image-utils]))

(defn test-image-exists [type name image]
  (testing (str type name "is available.")
    (is (not (nil? image))))
  (let [pil-image (image-utils/open-py-image (:local-file image))]
    (testing (str type name "is a readable image.")
      (is (not (nil? pil-image))))
    (testing (str type name "has a width > 0.")
      (is (> (image-utils/pil->w pil-image) 0)))
    (testing (str type name "has a height > 0.")
      (is (> (image-utils/pil->h pil-image) 0)))))
