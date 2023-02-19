(ns proomp.util.pipe-util-test
  (:require
    [clojure.test :refer :all]
    [proomp.util.pipe-utils :refer :all]
    [libpython-clj2.require :refer [require-python]]))

(require-python '[diffusers :refer [StableDiffusionPipeline StableDiffusionImg2ImgPipeline]])

(deftest ->stable-diffusion-pipeline-test
  (let [pipe (->text-to-image-pipeline)]
    (testing "Create StableDiffusionPipeline" (is (not (nil? pipe))))))

(deftest ->stable-diffusion-i2i-pipeline-test
  (let [pipe (->image-to-image-pipeline)]
    (testing "Create StableDiffusionImg2ImgPipeline" (is (not (nil? pipe))))))
