(ns lean-discussion.about.views
  (:require [re-frame.core :as re-frame]))

;; about

(defn about-description []
  [:p "All about lean-discussion"])

(defn about-panel []
  [:div.ui.padded.grid
   [:div.ui.centered.row
    [:div.ui.column
     [:div.spacer]
     [:div.ui.horizontal.divider.header "What is Lean Discussion?"]
     [about-description]]]])


