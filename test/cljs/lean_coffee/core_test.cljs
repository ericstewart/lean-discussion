(ns lean-coffee.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [lean-coffee.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
