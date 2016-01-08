(ns lean-coffee.db
  (:require [reagent.core :as reagent]))

(def default-db
  (reagent/atom {:name "Lean Caffeine"
                 :topics [
                          {:id "1" :label "Topic 1" :short "T1"}
                          {:id "2" :label "Topic 2" :short "T2"}
                          {:id "3" :label "Topic 3" :short "T3"}
                          ]}))

