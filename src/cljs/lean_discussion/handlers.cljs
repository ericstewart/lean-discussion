(ns lean-discussion.handlers
    (:require [re-frame.core :as re-frame]
              [lean-discussion.db :as db]
              [akiroz.re-frame.storage :refer [persist-db reg-co-fx!]]
              [clairvoyant.core :refer-macros [trace-forms]]
              [day8.re-frame.undo :as undo :refer [undoable]]
              [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

  ;; -- Interceptors --------------------------------------------------------

  ;; This interceptor stores topics into local storage
  ;; We attach it to each event handler which could update todos
  (reg-co-fx! :lean-discussion-store
              {:fx :store
               :cofx :store})

  ;; -- Helpers --------------------------------------------------------

  (defn my-reg-event-db
    [event-id interceptors handler]
    (re-frame/reg-event-fx
      event-id
      [(persist-db :lean-discussion-store :persistent) interceptors]
      (fn [{:keys [db]} event-vec]
        {:db (handler db event-vec)})))

  ;; -- Event Handlers --------------------------------------------------------

  (defn set-active-panel-handler
    "Change the active panel"
    [db [_ active-panel]]
    (assoc db :active-panel active-panel))

  (re-frame/reg-event-fx
    :initialize-db
    [(re-frame/inject-cofx :store)]
    (fn initialize-db-handler
      [{:keys [db store]} _]
      {:db (assoc @db/default-db :persistent store)}))

  (re-frame/reg-event-db
    :set-active-panel
    set-active-panel-handler)

  (re-frame/reg-event-db
    :set-session-mode
    (fn session-mode-handler
      [db [_ new-mode]]
      (assoc db :session-mode new-mode)))

  (my-reg-event-db
    :change-card-state
    (undoable "change card state")
    (fn change-card-state-handler
      [db [_ id new-state]]
      (let [topic-id (int id)
            original-state (get-in db [:persistent :topics (int topic-id) :state])]
        (-> db
          (update-in [:persistent :column-order original-state] (fnil disj #{}) topic-id)
          (update-in [:persistent :column-order new-state] (fnil conj #{}) topic-id)
          (assoc-in [:persistent :topics topic-id :state] new-state)))))

  (my-reg-event-db
    :add-new-topic
    (undoable "add new topic")
    (fn add-new-topic-handler
      [db [_ new_topic]]
      (let [next-id (inc (apply max (conj (keys (:topics (:persistent db))) 1)))]
        (-> db
          (assoc-in [:persistent :topics next-id] {:id next-id :label new_topic :state :to-do :votes 0})
          (update-in [:persistent :column-order :to-do] (fnil conj #{}) next-id)))))

  (my-reg-event-db
    :delete-topic
    (undoable "delete topic")
    (fn delete-topic-handler
      [db [_ topic-id]]
      (let [current-topic-state (get-in db [:persistent :topics (int topic-id) :state])]
        (-> db
          (update-in [:persistent :column-order current-topic-state] disj (int topic-id))
          (update-in [:persistent :topics] dissoc (int topic-id))))))

  (my-reg-event-db
    :clear-all-topics
    (undoable)
    (fn delete-topic-handler
      [db [_]]
      (-> db
          (update-in [:persistent :topics] {})
          (update-in [:persistent :column-order] {}))))


  (my-reg-event-db
    :vote-for-topic
    (undoable "topic vote")
    (fn vote-topic-handler
      [db [_ topic-id]]
      (update-in db [:persistent :topics (int topic-id) :votes] inc))))
