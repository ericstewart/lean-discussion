(ns lean-discussion.views
    (:require [cljs.pprint :refer [pprint]]
              [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [re-com.selection-list :refer [selection-list-args-desc]]
              [reagent.core :as reagent]
              [datafrisk.core :as datafrisk]
              [lean-discussion.modals :as modals]
              [lean-discussion.about.views :as about]
              [lean-discussion.collect-topics.views :as collect-views]
              [lean-discussion.topics.views :as topics-views]
              [clairvoyant.core :refer-macros [trace-forms]]
              [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "gold")}

  ;; home
  (defn home-title []
    (let [name (re-frame/subscribe [:name])]
      (fn []
        [:div.row
         [:h1 {:class "ui header center aligned"} (str "Hello from " @name ". This is the Home Page.")]])))



  (defn session-panel-board-render
    []
    (let [current-mode (re-frame/subscribe [:session-mode])]
     [:div#session-area.ui.shape.segment.container
       [:div.sides
        [:div#collect-topics {:class (str "ui side"
                                          (if (= :collect @current-mode)
                                            "active"))}
          [collect-views/collect-topics-view]]
        [:div#execute {:class (str "ui side"
                                   (if (= :execute @current-mode)
                                     "active"))}
         [:div {:class "ui center aligned three column stackable grid"}
          [:div#board {:class "ui vertically divided row"}
           [topics-views/session-panel-column "To-Do" :to-do]
           [topics-views/session-panel-column "Doing" :doing]
           [topics-views/session-panel-column "Done" :done]]]]]]))

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
         [:div.ui.ordered.three.tiny.steps
          [:a {:href "#collect"
               :class (str "link step"
                           (if (= :collect @current-mode)
                             " active"))}
           [:div.content
            [:div.title "Collect"]
            [:div.description "Collect potential discussion topics"]]]
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

  ;; Primary panels

  (defmulti panels identity)
  (defmethod panels :home-panel [] [home-panel])
  (defmethod panels :about-panel [] [about/about-panel])
  (defmethod panels :default [] [home-panel])

  (defn nav-panel2
    []
    (let [name (re-frame/subscribe [:name])
          active-panel (re-frame/subscribe [:active-panel])]
      [:div.ui.secondary.pointing.menu
       [:a {:class "item" :href "#"} @name]
       [:a {:href "#" :class (str "item" (if (= :home-panel @active-panel)
                                           " active"))} "Home"]
       [:a {:href "#about" :class (str "item" (if (= :about-panel @active-panel)
                                                " active"))} "About"]]))


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
         [:div
          [modals/modal-window]
          (panels @active-panel)
          [datafrisk/DataFriskShell @re-frame.db/app-db]]))))


