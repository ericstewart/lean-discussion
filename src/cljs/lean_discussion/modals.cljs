(ns lean-discussion.modals
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [goog.events :as events]
            [goog.object]
            [cljsjs.semantic-ui-react]))

;; Easy handle to the top-level extern for semantic-ui-react
(def semantic-ui js/semanticUIReact)

(defn component
  "Get a component from sematic-ui-react:
    (component \"Button\")
    (component \"Menu\" \"Item\")"
  [k & ks]
  (if (seq ks)
    (apply goog.object/getValueByKeys semantic-ui k ks)
    (goog.object/get semantic-ui k)))

(def modal (component "Modal"))
(def button (component "Button"))
(def modal-header (component "Modal" "Header"))
(def modal-content (component "Modal" "Content"))
(def modal-actions (component "Modal" "Actions"))

;; Support modal dialogs
(def modal-id "semantic-ui-modal")

(def modal-data (reagent/atom {:content [:div]}
                               :title "Title"
                               :actions [:div.actions]
                               :shown nil
                               :size nil))

(defn get-modal []
  (dom/getElement modal-id))

; (defn show-modal!
;   []
;   (let [m (js/$ (get-modal))]
;     (.log js/console (.modal m))
;     (.call (aget m "modal") m #js {:detachable false
;                                    :onShow (:show @modal-data)
;                                    :onDeny (:deny @modal-data)
;                                    :onApprove (:approve @modal-data)})
;     (.call (aget m "modal") m "show")))


(defn close-modal! []
  (let [m (js/$ (get-modal))]
    (.call (aget m "modal") m "hide")))

(defn close-button
  "A pre-configured close button. Just include it anywhere in the
   modal to let the user dismiss it." []
  [:button.close {:type "button" :data-dismiss "modal"}
   [:span.glyphicon.glyphicon-remove {:aria-hidden "true"}]
   [:span.sr-only "Close"]])

(defn modal-window []
  [modal])
  

; (defn modal-window* []
;   (let [{:keys [content title actions]} @modal-data]
;     [:div.ui.modal {:id modal-id}
;      [:div.header title]
;      [:div.content content]
;      actions]))

; (def modal-window
;   (with-meta
;     modal-window*
;     {:component-did-mount
;                    (fn [e] (let [m (js/$ (get-modal))]
;                              (.call (aget m "on") m "onHidden"
;                                     #(do (when-let [f (:hidden @modal-data)] (f))
;                                          (reset! modal-data {:content [:div]}))) ;;clear the modal when hidden
;                              (.call (aget m "on") m "onShow"
;                                     #(when-let [f (:shown @modal-data)] (f)))
;                              (.call (aget m "on") m "onHide"
;                                     #(when-let [f (:hide @modal-data)] (f)))))
;      :display-name "semantic modal"}))

; (defn modal!
;   "Update and show the modal window."
;   ([reagent-content] (modal! reagent-content nil))
;   ([reagent-content configs]
;    (reset! modal-data (merge {:content reagent-content} configs))
;    (show-modal!)))

(defn add-modal
  []
  (let [this (reagent/current-component) 
        modal-open (re-frame/subscribe [:modal-open])]
    [:> modal {:trigger (reagent/as-component [:> button {:onClick #(re-frame/dispatch [:show-modal true])} "Press Me"])
               :open @modal-open
               :onClose #(reset! modal-open false)}
      [:> modal-header "Add a Topic"]
      [:> modal-content "Foo"]
      [:> modal-actions 
        [:> button {:color "green" 
                    :onClick (fn [] 
                                (.log js/console (js/$ (reagent/dom-node this)))
                                (re-frame/dispatch [:show-modal false]))} 
         "Close"]]]))
    



;; <Modal.Actions>
        ;   <Button color='green' onClick={this.handleClose} inverted>
        ;     <Icon name='checkmark' /> Got it
        ;   </Button>
        ; </Modal.Actions>
