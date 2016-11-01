(ns lean-discussion.db
  (:require [reagent.core :as reagent]
            [cljs.spec :as s]))


;; -- Spec --------------------------------------------------------------------
;;
;; This is a clojure.spec specification for the value in app-db. It is like a
;; Schema. See: http://clojure.org/guides/spec
;;
;; The value in app-db should always match this spec. Only event handlers
;; can change the value in app-db so, after each event handler
;; has run, we re-check app-db for correctness (compliance with the Schema).
;;
;; How is this done? Look in events.cljs and you'll notice that all handers
;; have an "after" interceptor which does the spec re-check.
;;
;; None of this is strictly necessary. It could be omitted. But we find it
;; good practice.

(s/def ::id int?)
(s/def ::label string?)
(s/def ::state
  #{:to-do
    :doing
    :done})
(s/def ::votes int?)
(s/def ::topic (s/keys :req-un [::id ::label ::state]
                       :opt [::votes]))
(s/def ::topics (s/nilable (s/and
                             (s/map-of ::id ::topic))))
(s/def ::column-order (s/nilable (s/map-of ::state
                                   (s/coll-of ::id))))
(s/def ::persistent (s/nilable
                      (s/keys :opt-un [::topics ::column-order])))
(s/def ::db (s/keys :req-un [::name ::session-mode]
                    :opt-un [::persistent]))


;; -- Default app-db Value  ---------------------------------------------------
;;
;; When the application first starts, this will be the value put in app-db
;; Unless, or course, there are todos in the LocalStore
;; Look in core.cljs for  "(dispatch-sync [:initialise-db])"
;;

(def default-db
  (reagent/atom {:name "Lean Discussion"
                 :session-mode :collect
                 :persistent {:topics {1 {:id 1 :label "An Example Topic" :state :to-do :votes 0}
                                       2 {:id 2 :label "Another Example Topic" :state :to-do :votes 0}
                                       3 {:id 3 :label "Example of somethng to discuss" :state :to-do :votes 0}}
                              :column-order {:to-do #{3 1 2}
                                             :doing #{}
                                             :done #{}}}}))

