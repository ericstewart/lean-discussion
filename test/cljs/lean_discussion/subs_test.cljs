(ns lean-discussion.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [reagent.debug :refer-macros [dbg println log]]
            [reagent.core :as reagent]
            [lean-discussion.subs :as subs])
  (:require-macros [reagent.ratom :refer [reaction]]))

(deftest test-handler-sorted-topic
    (let [test-db (reagent/atom {:name "Lean Caffeine"
                                 :persistent {:topics {1 {:id 1 :label "An Example Topic" :state :to-do}
                                                       2 {:id 2 :label "Another Example Topic" :state :to-do}
                                                       3 {:id 3 :label "Example of somethng to discuss" :state :to-do}}
                                              :column-order {:to-do [3 1 2]}}
                                 :session-mode :collect})]
      (testing "doesn't retrieve topics with incorrect state"
        (is (= []
               (subs/sorted-topics-with-state @test-db :fake-topic))))
      (testing "does retrive topics of the right state"
        (is (= 3
               (count (subs/sorted-topics-with-state @test-db :to-do))))
        (is (= [3 1 2]
               (map #(:id %) (subs/sorted-topics-with-state @test-db :to-do)))))))

(deftest test-topics-sorted-by-sorting
  (let [test-db (reagent/atom {:name "Lean Caffeine"
                               :persistent {:topics {1 {:id 1 :label "An Example Topic" :state :to-do :votes 2}
                                                     2 {:id 2 :label "Another Example Topic" :state :to-do :votes 3}
                                                     3 {:id 3 :label "Example of somethng to discuss" :state :to-do :votes 1}}
                                            :column-order {:to-do [3 1 2]}}
                               :session-mode :collect})]
    (testing "returns in correct order"
      (let [results (subs/topics-sorted-by @test-db :to-do :votes)]
        (is (= [2 1 3]
               (into [] (map #(:id %))
                     results)))))))

(deftest test-topics-sorted-same-count
  (let [test-db (reagent/atom {:name "Lean Caffeine"
                               :persistent {:topics {1 {:id 1 :label "An Example Topic" :state :to-do :votes 3}
                                                     2 {:id 2 :label "Another Example Topic" :state :to-do :votes 3}
                                                     3 {:id 3 :label "Example of somethng to discuss" :state :to-do :votes 1}}
                                            :column-order {:to-do [3 1 2]}}
                               :session-mode :collect})]
    (testing "returns in correct order"
      (let [results (subs/topics-sorted-by @test-db :to-do :votes)]
        (is (= 3
               (count (vals results))))
        (is (= [2 1 3]
               (into [] (map #(:id %))
                     results)))))))
