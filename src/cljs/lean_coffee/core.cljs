(ns lean-coffee.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [lean-coffee.handlers]
              [lean-coffee.subs]
              [lean-coffee.routes :as routes]
              [lean-coffee.views :as views]
              [lean-coffee.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

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
  (mount-root)
  (mount-nav))
  ;(mount-footer))
