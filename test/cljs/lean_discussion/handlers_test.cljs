(ns lean-discussion.handlers-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [reagent.core :as reagent]
            [lean-discussion.handlers :as handlers]))


(deftest active-panel-change
  []
  (testing "handle changes in active panel"
    (let [test-db (reagent/atom {:active-panel :default-panel})]
      (is (= :new-panel
             (:active-panel (handlers/set-active-panel-handler @test-db
                                                               [:set-active-panel :new-panel])))))))

(deftest next-topic-id
  []
  (testing "allocate next topic from empty set"
    (let [topics {}]
      (is (= 1
             (handlers/next-topic-id topics)))))
  (testing "allocate next topic with consecutive entries"
    (let [topics {1 {:id 1}
                  2 {:id 2}
                  3 {:id 3}}]
      (is (= 4
             (handlers/next-topic-id topics)))))
  (testing "doesn't care about skipped keys"
    (let [topics {1 {:id 1}
                  2 {:id 2}
                  9 {:id 9}}]
      (is (= 10
             (handlers/next-topic-id topics))))))
