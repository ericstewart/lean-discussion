(ns lean-coffee.modals
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

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
