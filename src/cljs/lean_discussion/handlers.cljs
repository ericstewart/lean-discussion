(ns lean-discussion.handlers
    (:require [re-frame.core :as re-frame]
              [lean-discussion.db :as db]
              [clairvoyant.core :refer-macros [trace-forms]]
              [day8.re-frame.undo :as undo :refer [undoable]]
              [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

  (re-frame/reg-event-db
   :initialize-db
   (fn initialize-db-handler
     [_ _]
     (merge @db/default-db @db/stored-topics)))

  (def ->ls (re-frame/after db/topics->ls!)) ;; middleware to store topics into local storage

  (defn set-active-panel-handler
    [db [_ active-panel]]
    (assoc db :active-panel active-panel))

  (re-frame/reg-event-db
   :set-active-panel
   set-active-panel-handler)

  (re-frame/reg-event-db
    :set-session-mode
    (fn session-mode-handler
      [db [_ new-mode]]
      (assoc db :session-mode new-mode)))

  (re-frame/reg-event-db
    :change-card-state
    (undoable "change card state")
    (fn change-card-state-handler
      [db [_ id new-state]]
      (let [topic-id (int id)
            original-state (get-in db [:topics (int topic-id) :state])]
        (-> db
          (update-in [:column-order original-state] (fnil disj #{}) topic-id)
          (update-in [:column-order new-state] (fnil conj #{}) topic-id)
          (assoc-in [:topics topic-id :state] new-state)))))

  (re-frame/reg-event-db
    :add-new-topic
    (undoable "add new topic")
    (fn add-new-topic-handler
      [db [_ new_topic]]
      (let [next-id (inc (apply max (conj (keys (:topics db)) 1)))]
        (-> db
          (assoc-in [:topics next-id] {:id next-id :label new_topic :state :to-do :votes 0})
          (update-in [:column-order :to-do] (fnil conj #{}) next-id)))))

  (re-frame/reg-event-db
    :delete-topic
    (undoable "delete topic")
    (fn delete-topic-handler
      [db [_ topic-id]]
      (let [current-topic-state (get-in db [:topics (int topic-id) :state])]
        (-> db
          (update-in [:column-order current-topic-state] disj (int topic-id))
          (update-in [:topics] dissoc (int topic-id))))))

  (re-frame/reg-event-db
    :clear-all-topics
    (undoable)
    (fn delete-topic-handler
      [db [_]]
      (-> db
          (update :topics {})
          (update :column-order {}))))

  (re-frame/reg-event-db
    :vote-for-topic
    (undoable "topic vote")
    (fn vote-topic-handler
      [db [_ topic-id]]
      (update-in db [:topics (int topic-id) :votes] inc))))
