(ns lean-discussion.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [reagent.debug :refer-macros [dbg println log]]
            [reagent.core :as reagent]
            [lean-discussion.subs :as subs])
  (:require-macros [reagent.ratom :refer [reaction]]))

(deftest test-handler-sorted-topic
    (let [test-db (reagent/atom {:name "Lean Caffeine"
                                 :topics {1 {:id 1 :label "An Example Topic" :state :to-do}
                                          2 {:id 2 :label "Another Example Topic" :state :to-do}
                                          3 {:id 3 :label "Example of somethng to discuss" :state :to-do}}
                                 :column-order {:to-do [3 1 2]}
                                 :session-mode :collect})]
      (testing "doesn't retrieve topics with incorrect state"
        (is (= []
               (subs/sorted-topics-with-state @test-db :fake-topic))))
      (testing "does retrive topics of the right state"
        (is (= 3
               (count (subs/sorted-topics-with-state @test-db :to-do))))
        (is (= [3 1 2]
               (map #(:id %) (subs/sorted-topics-with-state @test-db :to-do)))))))


