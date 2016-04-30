(defproject lean-coffee "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [reagent "0.6.0-SNAPSHOT"]
                 [re-frame "0.7.0"]
                 [re-com "0.8.0"]
                 [secretary "1.2.3"]
                 [garden "1.3.2"]
                 [compojure "1.5.0"]
                 [ring "1.4.0"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-figwheel "0.5.0-6"]
            [lein-garden "0.2.6"]
            [lein-doo "0.1.6"]
            [lein-less "1.7.5"]]
            ;[venantius/ultra "0.4.1"]]



  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css/compiled"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler lean-coffee.handler/handler}

  :garden {:builds [{:id "screen"
                     :source-paths ["src/clj"]
                     :stylesheet lean-coffee.css/screen
                     :compiler {:output-to "resources/public/css/compiled/screen.css"
                                :pretty-print? true}}]}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "lean-coffee.core/mount-root"}
                        :compiler {:main lean-coffee.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs"]
                        :compiler {:output-to "resources/public/js/compiled/test.js"
                                   :main lean-coffee.runner
                                   :optimizations :none}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main lean-coffee.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :externs ["externs.js"]
                                   :closure-defines {goog.DEBUG false}
                                   :pretty-print false}}]})
