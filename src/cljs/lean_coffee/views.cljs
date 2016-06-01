(ns lean-coffee.views
    (:require [cljs.pprint :refer [pprint]]
              [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [re-com.selection-list :refer [selection-list-args-desc]]
              [reagent.core :as reagent]
              [datafrisk.core :as datafrisk]))



;; Support modal dialogs
(def modal-id "semantic-ui-modal")

(def modal-content (atom {:content [:div]
                          :shown nil
                          :size nil}))

(defn get-modal []
  (js/$ (str "#" modal-id)))

(defn show-modal!
  [keyboard]
  (let [m (get-modal)]
    (.log js/console "In show-modal!")
    (.log js/console m)
    (.log js/console @modal-content)
    (.modal m "show")
    m))

(defn close-modal! []
  (let [m (js/jQuery (get-modal))]
    (.call (aget m "modal") m "hide")))

(defn close-button
  "A pre-configured close button. Just include it anywhere in the
   modal to let the user dismiss it." []
  [:button.close {:type "button" :data-dismiss "modal"}
   [:span.glyphicon.glyphicon-remove {:aria-hidden "true"}]
   [:span.sr-only "Close"]])

(defn modal-window* []
  (let [content (:content @modal-content)]
    [:div.ui.modal {:id modal-id}
     [:div.header "Header"]
     [:div.content [:p "Foo!!!!"]]
     [:div.actions
      [:div.ui.cancel.button "Cancel"]
      [:div.ui.approve.button "Add"]]]))

(def modal-window
  (with-meta
    modal-window*
    {:component-did-mount
     (fn [e] (let [m (get-modal)]
               (.call (aget m "on") m "onHidden"
                      #(do (when-let [f (:hidden @modal-content)] (f))
                           (reset! modal-content {:content [:div]}))) ;;clear the modal when hidden
               (.call (aget m "on") m "onShow"
                      #(when-let [f (:shown @modal-content)] (f)))
               (.call (aget m "on") m "onHide"
                      #(when-let [f (:hide @modal-content)] (f)))))}))

(defn modal!
  "Update and show the modal window."
  ([reagent-content] (modal! reagent-content nil))
  ([reagent-content configs] (reset! modal-content (merge {:content reagent-content} configs))
                             (show-modal! (get configs :keyboard true))))

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


(defn add-item-dialog
  "Collect input for a new item"
  []
  (let [show? (reagent/atom false)
        form-data (reagent/atom {:topic "Default topic name"})
        save-form-data (reagent/atom nil)
        process-add (fn [event]
                     (reset! show? false)
                     (.log js/console "Submitted form data: " @form-data)
                     ;; Processed returned data here
                     false)
        process-cancel (fn [event]
                         (reset! form-data @save-form-data)
                         (reset! show? false)
                         (.log js/console "Cancelled form data" @form-data)
                         false)
        show-modal   (fn []
                      [:div.ui.modal {:id "add-item"}
                       [:i.close.icon]
                       [:div.header "Add a New Topic"]
                       [:div.actions
                        [:div.ui.black.deny.button
                         {:on-click process-cancel}
                         "Cancel"]
                        [:div.ui.positive.button
                         {:on-click process-add}
                         "Add"]]])]
     [:div
      [:button.ui.button {:on-click #(do
                                      (.log js/console "In on-click handler")
                                      (modal! [:div "Some message"]))}
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
   [modal-window]
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


