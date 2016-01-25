(ns lean-coffee.handlers
    (:require [re-frame.core :as re-frame]
              [lean-coffee.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   @db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/register-handler
  :set-session-mode
  (fn [db [_ new-mode]]
    (assoc db :session-mode new-mode)))

(re-frame/register-handler
  :change-card-state
  (fn [db [_ id new-state]]
    (assoc-in db [:topics id :state] new-state)))
