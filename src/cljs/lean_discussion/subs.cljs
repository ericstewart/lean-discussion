(ns lean-discussion.subs
  (:require [reagent.ratom :refer [make-reaction]]
            [re-frame.core :as re-frame]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

  (re-frame/register-sub
   :name
   (fn [db _]
     (make-reaction (fn name-subscription
                      []
                      (:name @db)))))

  (defn sorted-topics-with-state
    [db desired-state]
    (map (:topics db)
         (get-in db [:column-order desired-state])))

  (re-frame/register-sub
    :topics
    (fn [db [_ desired-state]]
      (.log js/console (str "Desired: " desired-state))
      (make-reaction (fn topics-subscription
                       []
                       (sorted-topics-with-state @db desired-state)))))

  (defn topics-sorted-by
    "Return topics from app state filtered by state and sorted by a field"
    [db filter-state sort-key]
    (let [topics (sorted-topics-with-state db filter-state)]
       (into (sorted-set-by (fn [x y]
                               (>= (:votes x)
                                   (:votes y))))
             topics)))


  (re-frame/register-sub
    :vote-sorted-topics
    (fn [db [_ desired-state]]
      (.log js/console (str "Desired: " desired-state))
      (make-reaction (fn topics-subscription-sorted
                       []
                       (topics-sorted-by @db desired-state :votes)))))

  (re-frame/register-sub
   :active-panel
   (fn [db _]
     (make-reaction (fn active-panel-subscription
                      []
                      (:active-panel @db)))))

  (re-frame/register-sub
    :session-mode
    (fn [db _]
      (make-reaction (fn session-mode-subscription
                       []
                       (:session-mode @db))))))
