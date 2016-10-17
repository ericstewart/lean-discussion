(ns lean-discussion.subs
  (:require [reagent.ratom :refer [make-reaction]]
            [re-frame.core :as re-frame]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

  (re-frame/reg-sub
   :name
   (fn [db _]
     (:name db)))

  (defn sorted-topics-with-state
    [db desired-state]
    (map (:topics (:persistent db))
         (get-in db [:persistent :column-order desired-state])))

  (re-frame/reg-sub
    :topics
    (fn [db [_ desired-state]]
      (sorted-topics-with-state db desired-state)))

  (defn topics-sorted-by
    "Return topics from app state filtered by state and sorted by a field"
    [db filter-state sort-key]
    (let [topics (sorted-topics-with-state db filter-state)]
       (into (sorted-set-by (fn [x y]
                               (>= (:votes x)
                                   (:votes y))))
             topics)))

  (re-frame/reg-sub
    :vote-sorted-topics
    (fn [db [_ desired-state]]
      (topics-sorted-by db desired-state :votes)))


  (re-frame/reg-sub
   :active-panel
   (fn [db _]
     (:active-panel db)))


  (re-frame/reg-sub
    :session-mode
    (fn [db _]
      (:session-mode db))))
