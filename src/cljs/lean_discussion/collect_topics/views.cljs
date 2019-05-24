
(ns lean-discussion.collect-topics.views
  (:require [lean-discussion.topics.views :as topic-views]
            [lean-discussion.utils.semantic-ui :as sui]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [cljs.pprint :refer [pprint]]))

(defn add-item-modal
  []
  (let [this (reagent/current-component) 
        modal-open (re-frame/subscribe [:modal-open])
        form-data (reagent/atom {:topic ""})]
    (fn []
      [:> sui/modal {:trigger (reagent/as-component [:> sui/button {:onClick #(re-frame/dispatch [:show-modal true])} [:i.add.circle.large.icon]])
                     :size "small"
                     :open @modal-open
                     :onClose #(reset! form-data {:topic nil})}
        [:> sui/modal-header "Add a Topic"]
        [:> sui/modal-content 
          [:> sui/form {:onSubmit (fn [e] 
                                    (.preventDefault e) 
                                    false)}
            [:> sui/form-field {:required true}
              [:label "Topic"]
              [:> sui/input {:type "text"
                             :name "topic"
                             :placeholder "A topic for discussion"
                             :onChange (fn [e data] 
                                        (.log js/console (aget data "value"))
                                        (swap! form-data assoc :topic (aget data "value")))
                             :value (:topic @form-data)}]]]]
        [:> sui/modal-actions
          [:> sui/button {:color "green" 
                          :onClick (fn [] 
                                     (re-frame/dispatch [:show-modal false])
                                     (reset! form-data {:topic ""}))}
           "Close"]
         [:> sui/button {:color "green" 
                         :type "submit"
                         :onClick (fn []
                                    (.log js/console "Add form data:" @form-data)
                                    (re-frame/dispatch [:add-new-topic (:topic @form-data)])
                                    (re-frame/dispatch [:show-modal false])
                                    (reset! form-data {:topic ""}))}
          "Add"]]])))



(defn collect-topics-view
  []
  (let [topics (re-frame/subscribe [:topics :to-do])]
    [:div
     [:div.ui.basic.center.aligned.segment
      [:div.ui.icon.buttons
       [add-item-modal]
       [:button.circular.ui.icon.button {:on-click #(re-frame/dispatch [:clear-all-topics])}
        [:i.trash.circle.large.icon]]]]
     [:div.ui.basic.segment {:class "collected-cards"}
      [:div.ui.cards
       (for [topic @topics]
         ^{:key topic} [topic-views/topic-component topic])]]]))
