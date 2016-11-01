(ns lean-discussion.handlers-test
  (:require [lean-discussion.handlers :as handlers]
            [lean-discussion.db :as db]
            [reagent.core :as reagent]
            [clojure.test :refer-macros [deftest testing is]]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [clojure.spec.test :as stest]))


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
    (let [topics {1 {:id 1 :label "First" :state :to-do}
                  2 {:id 2 :label "Second" :state :to-do}
                  3 {:id 3 :label "Third" :state :to-do}}]
      (is (= 4
             (handlers/next-topic-id topics)))))
  (testing "doesn't care about skipped keys"
    (let [topics {1 {:id 1 :label "First" :state :to-do}
                  2 {:id 2 :label "Second" :state :to-do}
                  9 {:id 9 :label "Third" :state :to-do}}]
      (is (= 10
             (handlers/next-topic-id topics))))))

;; Generate tests based on the specs
(stest/instrument `handlers/next-topic-id)
(stest/summarize-results (stest/check `handlers/next-topic-id))
