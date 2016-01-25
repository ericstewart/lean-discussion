(ns lean-coffee.db
  (:require [reagent.core :as reagent]))

(def default-db
  (reagent/atom {:name "Lean Caffeine"
                 :topics {"1" {:id "1" :label "An Example Topic" :state :to-do}
                          "2" {:id "2" :label "Another Example Topic" :state :to-do}
                          "3" {:id "3" :label "Example of somethng to discuss" :state :to-do}}
                 :session-mode :collect}))

