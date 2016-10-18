(ns lean-discussion.handlers
    (:require [lean-discussion.db :as db]
              [re-frame.core :as re-frame]
              [day8.re-frame.undo :as undo :refer [undoable]]
              [clojure.spec :as s]
              [akiroz.re-frame.storage :refer [persist-db reg-co-fx!]]))
              ;[re-frame-tracer.core :refer [tracer]]
              ;[clairvoyant.core :refer-macros [trace-forms trace-handlers]])

;;(trace-forms {:tracer (tracer :color "green")}

;; -- Interceptors --------------------------------------------------------

(defn check-and-throw
  "throw an exception if db doesn't match the spec."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (re-frame/after (partial check-and-throw :lean-discussion.db/db)))

(def lean-discussion-interceptors
  [check-spec-interceptor])

;; -- Helpers --------------------------------------------------------

(defn my-reg-event-db
  "Custom even handler that adds the side effect of persisting the
  database in browser local-storage"
  [event-id interceptors handler]
  (re-frame/reg-event-fx
    event-id
    (flatten [(persist-db :lean-discussion-store :persistent) interceptors lean-discussion-interceptors])
    (fn [{:keys [db]} event-vec]
      {:db (handler db event-vec)})))

(defn next-topic-id
  "Returns the next unused topic id, which is assumed to be
   one more than the current largest id."
  [topics]
  (inc (apply max (conj (keys topics) 0))))

(s/fdef next-topic-id
        :args (s/cat :topics ::db/topics)
        :ret ::db/id
        :fn #(< 0
                (% :ret)))

;; -- Event Handlers --------------------------------------------------------

;; Register the cofx for interacting with local storage (via re-frame-storage)
(reg-co-fx! :lean-discussion-store
            {:fx :store
             :cofx :store})

(defn set-active-panel-handler
  "Change the active panel"
  [db [_ active-panel]]
  (assoc db :active-panel active-panel))

(re-frame/reg-event-fx
  :initialize-db
  (flatten [(re-frame/inject-cofx :store) check-spec-interceptor])
  (fn initialize-db-handler
    [{:keys [db store]} _]
    {:db (assoc @db/default-db :persistent store)}))

(re-frame/reg-event-db
  :set-active-panel
  lean-discussion-interceptors
  set-active-panel-handler)

(re-frame/reg-event-db
  :set-session-mode
  lean-discussion-interceptors
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
    (let [next-id (next-topic-id (:topics (:persistent db)))]
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
    (update-in db [:persistent :topics (int topic-id) :votes] inc)))
