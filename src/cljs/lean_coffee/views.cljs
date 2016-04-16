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
       [:h1 {:class "ui header center aligned"} (str "Hello from " @name ". This is the Home Page.")]])))

(defn draggable-topic-render
  [topic]
  [:div {:class "ui centered red card text-center" :data-card_id (:id topic)}
   [:div {:class "content"}
    [:h4 {:class "header"} "Topic: " (:id topic)]
    [:p {:class "description-text"} (:label topic)]]])

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
      [:div {:class "ui one cards container"}
       (for [topic @topics]
         ^{:key topic} [topic-component topic])])))


(defn session-panel-column-render
  [title column-state]
  [:div {:class (str "ui center aligned column topic-column")}
   [:h3 {:class ""} title]
   [:hr]
   [:div.ui.hidden.divider]
   [topics-view column-state]])

(defn session-panel-column-did-mount
  [this]
  (let [new-state (nth (reagent/argv this) 2)]
    (.droppable (js/$ (reagent/dom-node this))
                #js {:accept ".card.ui-draggable"
                     :drop (fn [event, ui]
                             (re-frame/dispatch [:change-card-state (aget ui "draggable" "0" "dataset" "card_id") new-state]))})))

(defn session-panel-column
  [title column-state]
  (reagent/create-class {:reagent-render session-panel-column-render
                         :component-did-mount session-panel-column-did-mount}))


(defn session-panel-board-render
  []
  (let [current-mode (re-frame/subscribe [:session-mode])]
   [:div#session-area.ui.shape.segment.container
     [:div.sides
      [:div#collect-topics {:class (str "ui side"
                                        (if (= :collect @current-mode)
                                          "active"))}
       [:div.ui.cards
        [:div {:class "ui card text-center"}
         [:div {:class "content"}
          [:h4 {:class "header"} "Topic ? "]
          [:p {:class "description-text"}]]]]]
      [:div#execute {:class (str "ui side"
                                 (if (= :execute @current-mode)
                                   "active"))}
       [:div {:class "ui center aligned three column stackable grid"}
        [:div#board {:class "ui vertically divided row"}
         [session-panel-column "To-Do" :to-do]
         [session-panel-column "Doing" :doing]
         [session-panel-column "Done" :done]]]]]]))

(defn session-panel-board-did-mount
  [this]
  (.shape (js/$ (reagent/dom-node this))))

(defn session-panel-board
  []
  (reagent/create-class {:reagent-render session-panel-board-render
                         :component-did-mount session-panel-board-did-mount}))

(defn session-panel
  []
  (let [current-mode (re-frame/subscribe [:session-mode])]
    [:div.row.container
     [:div.fluid.grid
      [:div.row
       [:div.ui.horizontal.divider.header "Steps"]
       [:div.ui.ordered.three.steps
        [:a {:href "#collect"
             :class (str "link step"
                         (if (= :collect @current-mode)
                           " active"))}
         [:div.content
          [:div.title "Collect"]
          [:div.description "Collect potential discussion topics"]]]
        ;[:a.step.disabled
        ; [:div.content
        ;  [:div.title "Vote"]
        ;  [:div.description "Vote on topics to discuss"]]]
        [:a {:href "#execute"
             :class (str "link step"
                         (if (= :execute @current-mode)
                           " active"))}
         [:div.content
          [:div.title "Discuss"]
          [:div.description "Discuss topics as time allows"]]]]]

      [:div.ui.hidden.divider]
      [:div.ui.row
       [:div.ui.horizontal.divider.header "Board"]
       [session-panel-board]]]]))


(defn link-to-about-page []
  [:div.row
   [:a {:href "#/about"} "Go to About Page"]])


(defn home-panel []
  [:div {:class "ui grid container-fluid"}
   [home-title]
   [session-panel]
   [link-to-about-page]])


;; about

(defn about-title []
  [:h1 {:class "ui header"} "This is the About Page."])

(defn link-to-home-page []
  [:a {:href "#/"} "Go to Home Page"])

(defn about-panel []
  [:div
   [about-title]
   [link-to-home-page]])


;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn nav-panel2
  []
  (let [name (re-frame/subscribe [:name])
        active-panel (re-frame/subscribe [:active-panel])]
    [:div.ui.secondary.pointing.menu
     [:a {:href "#" :class (str "item" (if (= :home-panel @active-panel)
                                         " active"))} "Home"]
     [:a {:href "#about" :class (str "item" (if (= :about-panel @active-panel)
                                              " active"))} "About"]]))


(defn nav-panel
  []
  (let [name (re-frame/subscribe [:name])
        active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:nav {:class "navbar navbar-light bg-faded"}
       [:a {:class "navbar-brand" :href "#"} @name]
       [:ul {:class "nav navbar-nav"}
        [:li {:class (str "nav-item" (if (= :home-panel @active-panel)
                                         " active"))}
         [:a {:class "nav-link" :href "#"} "Home"]]
        [:li {:class (str "nav-item" (if (= :about-panel @active-panel)
                                         " active"))}
         [:a {:class "nav-link" :href "#about"} "About"]]]])))

(defn footer-panel
  []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:footer {:class "ui inverted vertical footer segment"}
       [:div {:class "ui center aligned container-fluid"}
        [:p {:class "text-muted"} "Footer for " @name]]])))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
       [:div (panels @active-panel)
        [:pre (with-out-str (pprint @re-frame.db/app-db))]])))

