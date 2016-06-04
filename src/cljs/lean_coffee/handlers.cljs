(ns lean-coffee.handlers
    (:require [re-frame.core :as re-frame]
              [lean-coffee.db :as db]
              [clairvoyant.core :refer-macros [trace-forms]]
              [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

  (re-frame/register-handler
   :initialize-db
   (fn initialize-db-handler
     [_ _]
     @db/default-db))

  (defn set-active-panel-handler
    [db [_ active-panel]]
    (assoc db :active-panel active-panel))

  (re-frame/register-handler
   :set-active-panel
   set-active-panel-handler)

  (re-frame/register-handler
    :set-session-mode
    (fn session-mode-handler
      [db [_ new-mode]]
      (assoc db :session-mode new-mode)))

  (re-frame/register-handler
    :change-card-state
    (fn change-card-state-handler
      [db [_ id new-state]]
      (assoc-in db [:topics (int id) :state] new-state)))

  (re-frame/register-handler
    :add-new-topic
    (fn add-new-topic-handler
      [db [_ new_topic]]
      (let [next-id (inc (apply max (keys (:topics db))))]
        (assoc-in db [:topics next-id] {:id next-id :label new_topic :state :to-do}))))

  (re-frame/register-handler
    :delete-topic
    (fn delete-topic-handler
      [db [_ topic-id]]
      (update-in db [:topics] dissoc (int topic-id)))))

