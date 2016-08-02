(ns lean-discussion.execute-discussion.views
  (:require [lean-discussion.topics.views :as topics-views]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]
            [cljs.pprint :refer [pprint]]))

(trace-forms {:tracer (tracer :color "gold")}

             (defn countdown-clock
               []
               (let [discussing-topic? (re-frame/subscribe [:discussing-topic?])]
                 (if discussing-topic?
                   [:> js/ReactCountdownClock {:seconds (* 60 1)
                                               :color "#3f3f3f"
                                               :size 100
                                               :onComplete #(js/alert "Timer finished")}])))

             (defn discussion-view
               []
               [:div {:class "ui center aligned three column stackable grid container"}
                [:div.ui.vertially.divided.two.column.row
                 [:div.column "Session Details"]
                 [:div.column [countdown-clock]]]
                [:div#board {:class "ui vertically divided row"
                             :style {:min-height "300px"}}
                 [topics-views/session-panel-column "To-Do" :to-do]
                 [topics-views/session-panel-column "Doing" :doing]
                 [topics-views/session-panel-column "Done" :done]]]))

