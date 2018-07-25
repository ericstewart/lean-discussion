(ns lean-discussion.utils.semantic-ui
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
(def form (component "Form"))
(def form-field (component "Form" "Field"))
(def input (component "Input"))
(def modal-header (component "Modal" "Header"))
(def modal-content (component "Modal" "Content"))
(def modal-actions (component "Modal" "Actions"))
