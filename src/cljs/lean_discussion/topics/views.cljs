(ns lean-discussion.topics.views
  (:require [lean-discussion.modals :as modals]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-com.selection-list :refer [selection-list-args-desc]]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]
            [cljs.pprint :refer [pprint]]))

(trace-forms {:tracer (tracer :color "orange")}


  (defn draggable-topic-render
    [topic]
    [:div {:class "ui centered raised card text-center" :data-card_id (:id topic)}
     [:div.content
      [:div.meta
       [:span (str (:state topic))]]
      [:p.description-text (:label topic)]]
     [:div {:class "extra content"}
      [:div.ui.horizontal.list
       [:div.item
        [:div.mini.ui.basic.icon.button
         {:on-click #(re-frame/dispatch [:delete-topic (:id topic)])}
         [:i.icon.trash]]
        [:div.mini.ui.labeled.button
         [:div.mini.ui.button
           {:on-click #(re-frame/dispatch [:vote-for-topic (:id topic)])}
          [:i.heart.icon]]
         [:div.ui.basic.label (:votes topic 0)]]]]]])

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
    (let [topics (re-frame/subscribe [:vote-sorted-topics state])]
      (println "Returned topics")
      (println @topics)
      (fn []
        [:div {:class "ui one cards container"}
         (for [topic @topics]
           ^{:key topic} [topic-component topic])])))

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
                                                      :class (str "ui center aligned column topic-column " (if @active-drop-target? "current-drop-target"))}
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
                                                                            (reset! active-drop-target? nil))}))}))))

