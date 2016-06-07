(ns lean-discussion.execute-discussion.views
  (:require [cljs.pprint :refer [pprint]]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [lean-discussion.views :as main-views]
            [re-com.selection-list :refer [selection-list-args-desc]]
            [lean-discussion.modals :as modals]
            [clairvoyant.core :refer-macros [trace-forms]]
            [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "gold")})


