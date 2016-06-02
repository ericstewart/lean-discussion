(ns lean-coffee.views
    (:require [cljs.pprint :refer [pprint]]
              [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [re-com.selection-list :refer [selection-list-args-desc]]
              [reagent.core :as reagent]
              [datafrisk.core :as datafrisk]
              [lean-coffee.modals :as modals]
              [lean-coffee.about.views :as about]))




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

(defn add-item-form
  [])


(defn add-item-dialog
  "Collect input for a new item"
  []
  (let [form-data (reagent/atom {:topic "Default topic name"})
        save-form-data (reagent/atom nil)
        process-add (fn [event]
                     (.log js/console "Submitted form data: " @form-data)
                     ;; Processed returned data here
                     (re-frame/dispatch [:add-new-topic (:topic @form-data)])
                     (reset! form-data @save-form-data)
                     true)
        process-cancel (fn [event]
                         (reset! form-data @save-form-data)
                         (.log js/console "Cancelled form data" @form-data)
                         true)
        topic-form   [:form.ui.form
                       [:div.field
                        [:label "Topic"]
                        [:input {:type "text"
                                 :name "topic"
                                 :placeholder "A topic for discussion"
                                 :on-change #(swap! form-data assoc :topic (-> % .-target .-value))}]]]]
     [:div
      [:button.ui.button {:on-click #(modals/modal! topic-form
                                                    {:title "Add a New Topic"
                                                     :actions [:div.actions
                                                               [:div.ui.black.deny.button
                                                                "Cancel"]
                                                               [:div.ui.positive.button
                                                                "Add"]]
                                                     :approve process-add
                                                     :deny process-cancel})}
       "Add Item"]]))



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
       [add-item-dialog]
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
   [modals/modal-window]
   [home-title]
   [session-panel]
   [link-to-about-page]])



;; main

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
        [datafrisk/DataFriskShell @re-frame.db/app-db]])))


