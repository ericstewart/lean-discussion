(ns lean-discussion.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {}]
  [:.level1 {:color "black"}]
  [:div.topic-column {:min-height "300px"}]
  [:div#collect-topics {:min-heihgt "300px"}])
