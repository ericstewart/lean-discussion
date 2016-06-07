(ns lean-discussion.execute-discussion.views
  (:require [lean-discussion.views :as main-views]
            [lean-discussion.modals :as modals]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]
            [cljs.pprint :refer [pprint]]))

(trace-forms {:tracer (tracer :color "gold")})


