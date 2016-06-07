(ns lean-discussion.about.views
  (:require [re-frame.core :as re-frame]))

;; about

(defn about-title []
  [:h1 {:class "ui header"} "This is the About Page."])

(defn link-to-home-page []
  [:a {:href "#/"} "Go to Home Page"])

(defn about-panel []
  [:div
   [about-title]
   [link-to-home-page]])


