(ns lean-discussion.modals
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [goog.events :as events]))

;; Support modal dialogs
(def modal-id "semantic-ui-modal")

(def modal-content (reagent/atom {:content [:div]}
                                 :title "Title"
                                 :actions [:div.actions]
                                 :shown nil
                                 :size nil))

(defn get-modal []
  (dom/getElement modal-id))

(defn show-modal!
  []
  (let [m (js/$ (get-modal))]
    (.call (aget m "modal") m #js {:detachable false
                                   :onShow (:show @modal-content)
                                   :onDeny (:deny @modal-content)
                                   :onApprove (:approve @modal-content)})
    (.call (aget m "modal") m "show")))


(defn close-modal! []
  (let [m (js/$ (get-modal))]
    (.call (aget m "modal") m "hide")))

(defn close-button
  "A pre-configured close button. Just include it anywhere in the
   modal to let the user dismiss it." []
  [:button.close {:type "button" :data-dismiss "modal"}
   [:span.glyphicon.glyphicon-remove {:aria-hidden "true"}]
   [:span.sr-only "Close"]])

(defn modal-window* []
  (let [{:keys [content title actions]} @modal-content]
    [:div.ui.modal {:id modal-id}
     [:div.header title]
     [:div.content content]
     actions]))

(def modal-window
  (with-meta
    modal-window*
    {:component-did-mount
                   (fn [e] (let [m (js/$ (get-modal))]
                             (.call (aget m "on") m "onHidden"
                                    #(do (when-let [f (:hidden @modal-content)] (f))
                                         (reset! modal-content {:content [:div]}))) ;;clear the modal when hidden
                             (.call (aget m "on") m "onShow"
                                    #(when-let [f (:shown @modal-content)] (f)))
                             (.call (aget m "on") m "onHide"
                                    #(when-let [f (:hide @modal-content)] (f)))))
     :display-name "semantic modal"}))

(defn modal!
  "Update and show the modal window."
  ([reagent-content] (modal! reagent-content nil))
  ([reagent-content configs]
   (reset! modal-content (merge {:content reagent-content} configs))
   (show-modal!)))
