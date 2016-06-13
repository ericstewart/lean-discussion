(ns lean-discussion.collect-topics.views
  (:require [lean-discussion.topics.views :as topic-views]
            [lean-discussion.modals :as modals]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]
            [cljs.pprint :refer [pprint]]))

(trace-forms {:tracer (tracer :color "gold")}


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
       [:i.add.circle.large.icon]]]))





 (defn collect-topics-view
   []
   (let [topics (re-frame/subscribe [:topics :to-do])]
     [:div.ui.segments {:style {:min-height "500px"}}
      [:div.ui.segment
       [:p "Here you will collect topic ideas for discussion"]]
      [:div.ui.segment
        [add-item-dialog]
        [:button.circular.ui.icon.button {:on-click #(re-frame/dispatch [:clear-all-topics])}
          [:i.trash.circle.large.icon]]
        [:div.ui.horizontal.divider]
        [:div.ui.cards
         (for [topic @topics]
           ^{:key topic} [topic-views/topic-component topic])]]])))
