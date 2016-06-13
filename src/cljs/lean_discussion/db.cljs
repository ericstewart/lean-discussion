(ns lean-discussion.db
  (:require [reagent.core :as reagent]))

(def default-db
  (reagent/atom {:name "Lean Discussion"
                 :topics {1 {:id 1 :label "An Example Topic" :state :to-do :votes 0}
                          2 {:id 2 :label "Another Example Topic" :state :to-do :votes 0}
                          3 {:id 3 :label "Example of somethng to discuss" :state :to-do :votes 0}}
                 :column-order {:to-do #{3 1 2}
                                :doing #{}
                                :done #{}}
                 :session-mode :collect}))

