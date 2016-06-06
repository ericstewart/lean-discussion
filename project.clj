(defproject lean-discussion "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [reagent "0.5.1"]
                 [re-frame "0.7.0"]
                 [re-com "0.8.0"]
                 [secretary "1.2.3"]
                 [garden "1.3.2"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [ring "1.4.0"]
                 [data-frisk-reagent "0.2.2"]
                 [org.clojars.stumitchell/clairvoyant "0.2.0"]
                 [day8/re-frame-tracer "0.1.1-SNAPSHOT"]
                 [binaryage/devtools "0.6.1"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-garden "0.2.6"]
            [lein-less "1.7.5"]]
  ;[venantius/ultra "0.4.1"]]


  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css/compiled"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler lean-discussion.handler/dev-handler}

  :garden {:builds [{:id "screen"
                     :source-paths ["src/clj"]
                     :stylesheet lean-discussion.css/screen
                     :compiler {:output-to "resources/public/css/compiled/screen.css"
                                :pretty-print? true}}]}

  :profiles {:dev {:dependendies [[binaryage/devtools "0.6.1"]]
                   :plugins [[lein-figwheel "0.5.4-SNAPSHOT"]
                             [lein-doo "0.1.6"]]}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "lean-discussion.core/mount-root"}
                        :compiler {:main lean-discussion.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :closure-defines {"goog.DEBUG" true
                                                     "clairvoyant.core.devmode" true}
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs"]
                        :compiler {:output-to "resources/public/js/compiled/test.js"
                                   :main lean-discussion.runner
                                   :optimizations :none}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main lean-discussion.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :closure-defines {"goog.DEBUG" false}
                                   :externs ["externs.js"]
                                   :pretty-print false}}]
              :test-commands {"unit" ["phantomjs"
                                      "resources/test/phantom/runner.js"
                                      "resources/test/test.html"]}}
  :main lean-discussion.server
  :aot lean-discussion.server
  :prep-tasks [["cljsbuild" "once" "min"] "compile"])

