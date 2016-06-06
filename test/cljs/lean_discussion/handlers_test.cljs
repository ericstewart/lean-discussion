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

