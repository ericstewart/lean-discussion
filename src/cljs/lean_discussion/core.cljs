(ns lean-discussion.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [lean-discussion.handlers]
              [lean-discussion.subs]
              [lean-discussion.routes :as routes]
              [lean-discussion.views :as views]
              [lean-discussion.config :as config]
              [cljsjs.jquery]
              [cljsjs.jquery-ui]
              [cljsjs.semantic-ui]
              [re-frisk.core :refer [enable-re-frisk!]]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)
    (enable-console-print!)))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn mount-nav []
  (reagent/render [views/nav-panel2]
                  (.getElementById js/document "nav-container")))

(defn mount-footer []
  (reagent/render [views/footer-panel]
                  (.getElementById js/document "footer-container")))

(defn ^:export init [] 
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  ;(enable-re-frisk!)
  (mount-root)
  (mount-nav))
  ;(mount-footer))

