(ns lean-coffee.views
    (:require [cljs.pprint :refer [pprint]]
              [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [re-com.selection-list :refer [selection-list-args-desc]]
              [reagent.core :as reagent]))


;; home

(defn home-title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label (str "Hello from " @name ". This is the Home Page.")
       :level :level1])))

(defn- options-with
  [width content multi-select? disabled? required? as-exclusions?]
  (fn []
    [re-com/v-box
     :width (str width "px")
     :gap      "20px"
     :align    :start
     :children [[re-com/title :level :level3 :label "Parameters"]
                [re-com/h-box
                 :gap      "15px"
                 :align    :start
                 :children [[re-com/checkbox
                             :label       [re-com/box :align :start :child [:code ":disabled?"]]
                             :model       disabled?
                             :on-change   #(reset! disabled? %)]
                            [re-com/checkbox
                             :label       [re-com/box :align :start :child [:code ":multi-select?"]]
                             :model       multi-select?
                             :on-change   #(reset! multi-select? %)]
                            [re-com/checkbox
                             :label       [re-com/box :align :start :child [:code ":required?"]]
                             :model       required?
                             :on-change   #(reset! required? %)]
                            [re-com/checkbox
                             :label       [re-com/box :align :start :child [:code ":as-exclusions?"]]
                             :model       as-exclusions?
                             :on-change   #(reset! as-exclusions? %)]]]
                content]]))



(defn- list-with-options
  [width]
  (let [disabled? (reagent/atom false)
        multi-select? (reagent/atom true)
        required? (reagent/atom true)
        as-exclusions? (reagent/atom false)
        old-items (reagent/atom [{:id "1" :label "1st RULE: You do not talk about FIGHT CLUB." :short "1st RULE"}
                             {:id "2" :label "2nd RULE: You DO NOT talk about FIGHT CLUB." :short "2nd RULE"}
                             {:id "3" :label "3rd RULE: If someone says \"stop\" or goes limp, taps out the fight is over." :short "3rd RULE"}
                             {:id "4" :label "4th RULE: Only two guys to a fight." :short "4th RULE"}
                             {:id "5" :label "5th RULE: One fight at a time." :short "5th RULE"}
                             {:id "6" :label "6th RULE: No shirts, no shoes." :short "6th RULE"}
                             {:id "7" :label "7th RULE: Fights will go on as long as they have to." :short "7th RULE"}
                             {:id "8" :label "8th RULE: If this is your first night at FIGHT CLUB, you HAVE to fight." :short "8th RULE"}])
        items (re-frame/subscribe [:topics])
        selections (reagent/atom (set ["2"]))]  ;; (second @items)
    (fn []
    [options-with
     width
     [re-com/v-box ;; TODO: v-box required to constrain height of internal border.
      :children [[re-com/selection-list
                  :width          "450px"      ;; manual hack for width of variation panel A+B 1024px
                  :max-height     "200px"       ;; based on compact style @ 19px x 5 rows
                  :model          selections
                  :choices        items
                  :label-fn       :label
                  :as-exclusions? as-exclusions?
                  :multi-select?  multi-select?
                  :disabled?      disabled?
                  :required?      required?
                  :on-change      #(reset! selections %)]]]
     multi-select?
     disabled?
     required?
     as-exclusions?])))

(defn session-panel
  []
  [re-com/v-box
   :size     "auto"
   :gap      "10px"
   :children [
              [re-com/h-box
               :gap      "100px"
               :children [[re-com/v-box
                           :gap      "10px"
                           :width    "400px"
                           :children [[re-com/title :label "Notes" :level :level2]
                                      [re-com/p "Allows the user to select items from a list (single or multi)."]
                                      [re-com/p "Uses radio buttons when single selecting, and checkboxes when multi-selecting."]
                                      [re-com/p "Via strike-through, it supports the notion of selections representing exclusions, rather than inclusions."] ]]
                          [list-with-options 600]]]]])


(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])


(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title] [session-panel] [link-to-about-page]]])


;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])


;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [(panels @active-panel)
                  [:pre (with-out-str (pprint @re-frame.db/app-db))]]])))


(defn nav-panel []
  (let [name (re-frame/subscribe [:name])
        active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:nav {:class "navbar navbar-default navbar-fixed-top"}
       [:div {:class "container"}
        [:div {:class "navbar-header"}
         [:button {:type "button"
                   :class "navbar-toggle collapsed"
                   :data-toggle "collape"
                   :data-target "#navbar"
                   :aria-expanded "false"
                   :aria-controls "navbar"}
          [:span {:class "sr-only"} "Toggle navigation"]
          [:span {:class "icon-bar"}]
          [:span {:class "icon-bar"}]
          [:span {:class "icon-bar"}]]
         [:a {:class "navbar-brand" :href "#"} @name]]
        [:div {:id "navbar" :class "collapse navbar-collapse"}
         [:ul {:class "nav navbar-nav"}
          [:li (if (= :home-panel @active-panel) {:class "active"})
           [:a {:href "#"} "Home"]]
          [:li (if (= :about-panel @active-panel) {:class "active"})
           [:a {:href "#about"} "About"]]
          [:li {:class "dropdown"}
           [:a {:href "#" :class "dropdown-toggle" :data-toggle "dropdown" :role "button" :aria-haspopup "true" :aria-expanded "false"} "Dropdown" [:span {:class "caret"}]]
           [:ul {:class "dropdown-menu"}
            [:li [:a {:href "#"} "Action"]]
            [:li [:a {:href "#"} "Another Action"]]]]]]]])))

(defn footer-panel
  []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:footer {:class "footer"}
       [:div {:class "container"}
        [:p {:class "text-muted"} "Footer content here"]]])))
