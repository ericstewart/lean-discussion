(ns lean-discussion.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {}]
  [:.level1 {:color "black"}]
  [:div.topic-column {:min-height "300px"}]
  [:div#collect-topics {:min-height "300px"}]
  [:div#session-area {:height "100%"}]
  [:div.ui.segment.collected-cards {:min-height "500px"
                                    :background "WhiteSmoke"}])
