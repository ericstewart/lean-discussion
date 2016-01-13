(ns lean-coffee.views
    (:require [cljs.pprint :refer [pprint]]
              [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [re-com.selection-list :refer [selection-list-args-desc]]
              [reagent.core :as reagent]))


;; home

(defn home-title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div.row
       [:h1 (str "Hello from " @name ". This is the Home Page.")]])))

(defn draggable-topic-render
  [topic]
  [:div {:class "card card-block text-xs-center" :data-card_id (:id topic)}
    [:h4 {:class "card-title"} "Topic: " (:id topic) ]
    [:p {:class "card-text"} (:label topic)]])

(defn draggable-topic-did-mount
  [this]
  (.draggable (js/$ (reagent/dom-node this))
              #js {:snap ".topic-column"
                   :revert "invalid"
                   :stack "#board"}))

(defn topic-component
  [topic]
  (reagent/create-class {:reagent-render draggable-topic-render
                         :component-did-mount draggable-topic-did-mount}))

(defn topics-view
  [state]
  (let [topics (re-frame/subscribe [:topics state])]
    (fn []
      [:div {:class "card-view"}
       (for [topic @topics]
         ^{:key topic} [topic-component topic])])))


(defn session-panel-column-render
  [title column-state]
    [:div {:class (str "column-xs-12 col-md-4 topic-column")}
     [:h3 {:class "text-xs-center"} title]
     [:hr]
     [topics-view column-state]])

(defn session-panel-column-did-mount
  [this]
  (let [props (-> this :props)
        new-state (nth (reagent/argv this) 2)]
    (.droppable (js/$ (reagent/dom-node this))
                #js {:accept ".card.ui-draggable"
                     :drop (fn [event, ui]
                             (re-frame/dispatch [:change-card-state (aget ui "draggable" "0" "dataset" "card_id") new-state]))})))

(defn session-panel-column
  [title column-state]
  (reagent/create-class {:reagent-render session-panel-column-render
                         :component-did-mount session-panel-column-did-mount}))

(defn session-panel
  []

   [:div.row
    [:div.jumbotron
     [:h3 "Instructions"]
     [:p "Allows the user to select items from a list (single or multi)."]
     [:p "Uses radio buttons when single selecting, and checkboxes when multi-selecting."]
     [:p "Via strike-through, it supports the notion of selections representing exclusions, rather than inclusions."]]
   [:div#board {:class "row"}
    [session-panel-column "To-Do" :to-do]
    [session-panel-column "Doing" :doing]
    [session-panel-column "Done" :done]]])


(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])


(defn home-panel []
  [:div
   [home-title] [session-panel] [link-to-about-page]])


;; about

(defn about-title []
  [:h1 "This is the About Page."])

(defn link-to-home-page []
  [:a {:href "#/"} "Go to Home Page"])

(defn about-panel []
  [:div.row
   [about-title]
   [link-to-home-page]])


;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])



(defn nav-panel []
  (let [name (re-frame/subscribe [:name])
        active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:nav {:class "navbar navbar-light bg-faded"}
       [:a {:class "navbar-brand" :href "#"} @name ]
       [:ul {:class "nav navbar-nav"}
        [:li {:class (str "nav-item" (if (= :home-panel @active-panel)
                                              " active"))}
         [:a {:class "nav-link" :href "#"} "Home"]]
        [:li {:class (str "nav-item" (if (= :about-panel @active-panel)
                                              " active")) }
        [:a {:class "nav-link" :href "#about"} "About"]]]])))

(defn footer-panel
  []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:footer {:class "ui inverted vertical footer segmentfooter"}
       [:div {:class "ui center aligned container"}
        [:p {:class "text-muted"} "Footer for " @name]]])))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
       [:div.row
        (panels @active-panel)]
       [:div.row
        [:pre (with-out-str (pprint @re-frame.db/app-db))]]])))
