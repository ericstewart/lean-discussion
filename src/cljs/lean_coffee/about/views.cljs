(ns lean-coffee.about.views
  (:require [cljs.pprint :refer [pprint]]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [re-com.selection-list :refer [selection-list-args-desc]]
            [reagent.core :as reagent]))

;; about

(defn about-title []
  [:h1 {:class "ui header"} "This is the About Page."])

(defn link-to-home-page []
  [:a {:href "#/"} "Go to Home Page"])

(defn about-panel []
  [:div
   [about-title]
   [link-to-home-page]])


