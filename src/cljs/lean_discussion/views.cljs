(ns lean-discussion.views
    (:require [cljs.pprint :refer [pprint]]
              [lean-discussion.modals :as modals]
              [lean-discussion.about.views :as about]
              [lean-discussion.collect-topics.views :as collect-views]
              [lean-discussion.execute-discussion.views :as execute-discussion]
              [lean-discussion.topics.views :as topics-views]
              [re-frame.core :as re-frame]
              [reagent.core :as reagent]
              [clairvoyant.core :refer-macros [trace-forms]]
              [re-frame-tracer.core :refer [tracer]]
              [datafrisk.core :as datafrisk]
              [ReactCountdownClock :as countdown]))

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
     [:div#session-area.ui.shape.container.fluid
       [:div.ui.sides
        [:div#collect-topics {:class (str "ui side"
                                          (if (= :collect @current-mode)
                                            " active"))}
          [:div.ui.horizontal.divider.header "Collect Topics"]
          [collect-views/collect-topics-view]]
        [:div#discuss-topics {:class (str "ui side"
                                          (if (= :discuss @current-mode)
                                            " active"))}
          [:div.ui.horizontal.divider.header "Discuss Topics"]
          [execute-discussion/discussion-view]]]]))

  (defn session-panel-board-did-mount
    [this]
    (.shape (js/$ (reagent/dom-node this))))

  (defn session-panel-board
    []
    (reagent/create-class {:reagent-render session-panel-board-render
                           :component-did-mount session-panel-board-did-mount}))


  (defn steps-nav-row
    "Step-guided navigation for a discussion"
    []
    (let [current-mode (re-frame/subscribe [:session-mode])
          topics-to-discuss (re-frame/subscribe [:vote-sorted-topics :to-do])]
     [:div.ui.row
      [:div.column
        [:div.spacer]
        [:div.ui.horizontal.divider.header "Steps"]
        [:div.ui.ordered.three.tiny.top.attached.steps
         [:a {:href "#collect"
              :class (str "link step"
                          (if (= :collect @current-mode)
                            " active")
                          (if (> (count @topics-to-discuss) 0)
                            " completed"))}
          [:div.content
           [:div.title "Collect"]
           [:div.description "Collect potential discussion topics"]]]
         [:a {:href "#discuss"
              :class (str "link step"
                          (if (= :discuss @current-mode)
                            " active"))}
          [:div.content
           [:div.title "Discuss"]
           [:div.description "Discuss topics as time allows"]]]]]]))


  (defn home-panel
    "Primary view for a discussion/session"
    []
    [:div.ui.padded.grid
     [steps-nav-row]
     [:div.ui.hidden.divider]
     [:div.ui.row
      [:div.ui.column
        [session-panel-board]]]])


  ;; Primary panels
  (defmulti panels identity)
  (defmethod panels :home-panel [] [home-panel])
  (defmethod panels :about-panel [] [about/about-panel])
  (defmethod panels :default [] [home-panel])

  (defn nav-panel-render
    []
    (let [name (re-frame/subscribe [:name])
          active-panel (re-frame/subscribe [:active-panel])
          undos? (re-frame/subscribe [:undos?])
          redos? (re-frame/subscribe [:redos?])]
     [:div.ui.inverted.menu
      [:a {:class "item" :href "#"} @name]
      [:a {:href "#" :class (str "item" (if (= :home-panel @active-panel)
                                          " active"))} "Home"]
      [:a {:href "#about" :class (str "item" (if (= :about-panel @active-panel)
                                               " active"))} "About"]
      [:div.ui.right.dropdown.item
       "Actions"
       [:i.dropdown.icon]
       [:div.menu
        [:div.ui.button.item {:on-click #(re-frame/dispatch [:undo])
                              :class (str (if-not @undos?
                                           "disabled"
                                           ""))}
         "Undo"]
        [:div.ui.button.item {:on-click #(re-frame/dispatch [:redo])
                              :class (str (if-not @redos?
                                            "disabled"
                                            ""))}
         "Redo"]]]]))

  (defn nav-panel-did-mount
    [component]
    (let [dropdown (.find (js/$ (reagent/dom-node component)) "div.ui.right.dropdown.item")]
      (.call (aget (js/$ dropdown) "dropdown") dropdown #js {:on "hover"
                                                             :action "hide"})))


  (defn nav-panel2
    []
    (reagent/create-class {:reagent-render nav-panel-render
                             :component-did-mount nav-panel-did-mount}))


  (defn footer-panel
    []
    (let [name (re-frame/subscribe [:name])]
      (fn []
        [:footer {:class "ui inverted vertical footer segment"}
          [:div {:class "ui center aligned container-fluid"}
            [:p {:class "text-muted"} "Footer for " @name]]])))


  (defn main-panel []
    "Primary layout panel for the non-navigation area of the application"
    (let [active-panel (re-frame/subscribe [:active-panel])]
      (fn []
         [:div
          [modals/modal-window]
          (panels @active-panel)
          [datafrisk/DataFriskShell @re-frame.db/app-db]]))))


