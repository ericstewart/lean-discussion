(ns lean-coffee.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [reagent.debug :refer-macros [dbg println log]]
            [reagent.core :as reagent]
            [lean-coffee.subs :as subs])
  (:require-macros [reagent.ratom :refer [reaction]]))

(deftest filters-topics
  (testing "subscript handler for filtered topics"
    (let [test-db (reagent/atom {:name "Lean Caffeine"
                                 :topics {"1" {:id "1" :label "An Example Topic" :state :to-do}
                                          "2" {:id "2" :label "Another Example Topic" :state :to-do}
                                          "3" {:id "3" :label "Example of somethng to discuss" :state :to-do}}
                                 :session-mode :collect})]
      (is (= []
             (subs/sorted-topics-with-state test-db :fake-topic))))))


