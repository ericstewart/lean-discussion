(ns lean-discussion.views
    (:require [cljs.pprint :refer [pprint]]
              [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [re-com.selection-list :refer [selection-list-args-desc]]
              [reagent.core :as reagent]
              [datafrisk.core :as datafrisk]
              [lean-discussion.modals :as modals]
              [lean-discussion.about.views :as about]
              [clairvoyant.core :refer-macros [trace-forms]]
              [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "gold")}

  ;; home
  (defn home-title []
    (let [name (re-frame/subscribe [:name])]
      (fn []
        [:div.row
         [:h1 {:class "ui header center aligned"} (str "Hello from " @name ". This is the Home Page.")]])))

  (defn draggable-topic-render
    [topic]
    [:div {:class "ui centered card text-center" :data-card_id (:id topic)}
     [:div.content
      ;[:div.header "Topic: " (:id topic)]
      [:div.meta
       [:span (str (:state topic))]]
      [:p.description-text (:label topic)]]
     [:div {:class "extra content"}
      [:div.ui.horizontal.list
       [:div.item
        [:div.circular.mini.ui.basic.icon.button
          {:on-click #(re-frame/dispatch [:delete-topic (:id topic)])}
          [:i.icon.trash]]]]]])

  (defn draggable-topic-did-mount
    [this]
    (let [t (js/$ this)]
      (do
        (.draggable (js/$ (reagent/dom-node this))
                    #js {:snap ".topic-column"
                         :revert "invalid"
                         :stack "#board"}))))
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
    (let [form-data (reagent/atom {:topic nil})
          save-form-data (reagent/atom nil)
          show-handler (fn [event]
                        (.call (aget (js/$ "form") "form") (js/$ "form") #js {:keyboardShortcuts false}))

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
          topic-form   (fn []
                         [:form.ui.small.form
                           [:div.required.field
                            [:label "Topic"]
                            [:input {:type "text"
                                     :name "topic"
                                     :placeholder "A topic for discussion"
                                     :value (:topic @form-data)
                                     :on-change #(swap! form-data assoc :topic (-> % .-target .-value))}]]])]
      [:div
       [:button.circular.ui.icon.button {:on-click #(modals/modal! [topic-form]
                                                     {:title "Add a New Topic"
                                                      :actions [:div.actions
                                                                [:div.ui.black.deny.button
                                                                 "Cancel"]
                                                                [:div.ui.positive.button
                                                                 "Add"]]
                                                      :show show-handler
                                                      :approve process-add
                                                      :deny process-cancel})}
        [:i.add.circle.icon]]]))



  (defn session-panel-column-render
    [title column-state active-drop-target?]
    [:div {:id (str (name column-state) "-column")
           :class (str "ui center aligned column topic-column")}
     [:h3 {:class ""} title]
     [:hr]
     [:div.ui.hidden.divider]
     [topics-view column-state]])

  (defn session-panel-column-did-mount
    [this]
    (let [new-state (nth (reagent/argv this) 2)
          drop-container-parent (js/$ (reagent/dom-node this))
          drop-container (.call (aget drop-container-parent "find") drop-container-parent "ui.cards.container")]
      (.droppable drop-container-parent
                  #js {:accept ".card.ui-draggable"
                       :drop (fn [event, ui]
                               (re-frame/dispatch [:change-card-state (aget ui "draggable" "0" "dataset" "card_id") new-state]))
                       :over (fn [event, ui]
                               (.log js/console (str "An acceptable card is over the column: " new-state)))
                       :out (fn [event, ui]
                               (.log js/console (str "An acceptable card is no longer over the column: " new-state)))})))

  (defn session-panel-column
    [title column-state]
    (let [active-drop-target? (reagent/atom nil)]
      (reagent/create-class {:reagent-render (fn [title column-state]
                                               [:div {:id (str (name column-state) "-column")
                                                      :class (str "ui center aligned column topic-column " (if @active-drop-target? "green"))}
                                                [:h3 {:class ""} title]
                                                [:hr]
                                                [:div.ui.hidden.divider]
                                                [topics-view column-state @active-drop-target?]])
                             :component-did-mount (fn [this]
                                                    (.droppable (js/$ (reagent/dom-node this))
                                                                #js {:accept ".card.ui-draggable"
                                                                     :drop (fn [event, ui]
                                                                             (re-frame/dispatch [:change-card-state (aget ui "draggable" "0" "dataset" "card_id") column-state])
                                                                             (reset! active-drop-target? nil))
                                                                     :over (fn [event, ui]
                                                                             (.log js/console (str "An acceptable card is over the column: " column-state))
                                                                             (reset! active-drop-target? true))
                                                                     :out (fn [event, ui]
                                                                            (.log js/console (str "An acceptable card is no longer over the column: " column-state))
                                                                            (reset! active-drop-target? nil))}))})))

  (defn collect-topics-view
    []
    (let [topics (re-frame/subscribe [:topics :to-do])]
     [:div
       [add-item-dialog]
       [:div.ui.horizontal.divider]
       [:div.ui.cards
        (for [topic @topics]
          ^{:key topic} [topic-component topic])]]))


  (defn session-panel-board-render
    []
    (let [current-mode (re-frame/subscribe [:session-mode])]
     [:div#session-area.ui.shape.segment.container
       [:div.sides
        [:div#collect-topics {:class (str "ui side"
                                          (if (= :collect @current-mode)
                                            "active"))}
          [collect-topics-view]]
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



  ;(defn nav-panel
  ;  []
  ;  (let [name (re-frame/subscribe [:name])
  ;        active-panel (re-frame/subscribe [:active-panel])]
  ;    (fn []
  ;      [:nav {:class "navbar navbar-light bg-faded"}
  ;       [:a {:class "navbar-brand" :href "#"} @name]
  ;       [:ul {:class "nav navbar-nav"}
  ;        [:li {:class (str "nav-item" (if (= :home-panel @active-panel)
  ;                                         " active"))}
  ;         [:a {:class "nav-link" :href "#"} "Home"]]
  ;        [:li {:class (str "nav-item" (if (= :about-panel @active-panel)
  ;                                         " active"))}
  ;         [:a {:class "nav-link" :href "#about"} "About"]]]])))

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


