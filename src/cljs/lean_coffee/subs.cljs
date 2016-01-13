(ns lean-coffee.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :name
 (fn [db _]
   (reaction (:name @db))))

(re-frame/register-sub
  :topics
  (fn [db [_ desired-state]]
    (.log js/console (str "Desired: " desired-state))
    (reaction
      (let [topics (filter (fn [x] (= desired-state (:state x))) (vals (:topics @db)))]
        (sort-by :label topics)))))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

