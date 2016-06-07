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
