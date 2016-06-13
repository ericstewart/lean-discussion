(ns lean-discussion.db
  (:require [reagent.core :as reagent]
            [alandipert.storage-atom :refer [local-storage]]))

(def default-db
  (reagent/atom {:name "Lean Discussion"
                 :topics {1 {:id 1 :label "An Example Topic" :state :to-do :votes 0}
                          2 {:id 2 :label "Another Example Topic" :state :to-do :votes 0}
                          3 {:id 3 :label "Example of somethng to discuss" :state :to-do :votes 0}}
                 :column-order {:to-do #{3 1 2}
                                :doing #{}
                                :done #{}}
                 :session-mode :collect}))

(def lstopics "lean-discussion-topics")
(def stored-topics (local-storage (atom {}) lstopics))

(defn ls->topics
  "Read in topics from local storage and process into a map we can merge into app-db"
  []
  @stored-topics)

(defn topics->ls!
  "Put topics into local storage"
  [db]
  (swap! stored-topics assoc :topics (:topics db))
  (swap! stored-topics assoc :column-order (:column-order db)))
