(ns lean-coffee.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {}]
  [:.level1 {:color "black"}]
  [:div.topic-column {:min-height "300px"}])
