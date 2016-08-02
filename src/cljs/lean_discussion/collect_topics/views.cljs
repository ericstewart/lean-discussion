(ns lean-discussion.collect-topics.views
  (:require [lean-discussion.topics.views :as topic-views]
            [lean-discussion.modals :as modals]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [goog.dom :as dom]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]
            [cljs.pprint :refer [pprint]]))

(trace-forms {:tracer (tracer :color "gold")}


 (defn add-item-button
   "Collect input for a new item"
   []
   (let [form-data (reagent/atom {:topic nil})
         save-form-data (reagent/atom nil)
         show-handler (fn [event]
                        (let [f (js/$ (dom/getElement "form"))]
                          (.call (aget f "form")  
                                 "form" 
                                 #js {:keyboardShortcuts false})))
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
      [:button.circular.ui.icon.button {:on-click #(modals/modal! [topic-form]
                                                                  {:title "Add a New Topic"
                                                                   :actions [:div.actions
                                                                             [:div.ui.buttons
                                                                               [:div.ui.black.deny.button
                                                                                "Cancel"]
                                                                               [:div.or]
                                                                               [:div.ui.positive.button
                                                                                "Add"]]]
                                                                   :show show-handler
                                                                   :approve process-add
                                                                   :deny process-cancel})}
       [:i.add.circle.large.icon]]))





 (defn collect-topics-view
   []
   (let [topics (re-frame/subscribe [:topics :to-do])]
     [:div
      [:div.ui.basic.center.aligned.segment
        [:div.ui.icon.buttons
         [add-item-button]
         [:button.circular.ui.icon.button {:on-click #(re-frame/dispatch [:clear-all-topics])}
          [:i.trash.circle.large.icon]]]]
      [:div.ui.basic.segment {:class "collected-cards"}
        [:div.ui.cards
         (for [topic @topics]
           ^{:key topic} [topic-views/topic-component topic])]]])))
