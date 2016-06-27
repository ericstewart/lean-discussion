(defproject lean-discussion "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.14"]
                 [reagent "0.6.0-rc"]
                 [re-frame "0.7.0"]
                 [re-com "0.8.3"]
                 [secretary "1.2.3"]
                 [garden "1.3.2"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [ring "1.4.0"]
                 [binaryage/devtools "0.7.0"]
                 [alandipert/storage-atom "2.0.1"]
                 [data-frisk-reagent "0.2.2"]
                 [org.clojars.stumitchell/clairvoyant "0.2.0"]
                 [day8/re-frame-tracer "0.1.1-SNAPSHOT"]]


  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "script"]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-garden "0.2.6"]
            [lein-less "1.7.5"]
            [lein-npm "0.6.2"]]


  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css/compiled"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler lean-discussion.handler/dev-handler
             :open-file-command "open-in-intellij"}

  :garden {:builds [{:id "screen"
                     :source-paths ["src/clj"]
                     :stylesheet lean-discussion.css/screen
                     :compiler {:output-to "resources/public/css/screen.css"
                                :pretty-print? true}}]}

  :profiles {:dev {:dependencies [
                                  [figwheel-sidecar "0.5.4-5" :exclusions [org.cljure/clojurescript]]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.12"]]
                   :plugins [[lein-figwheel "0.5.4-5"]
                             [lein-doo "0.1.6"]]}
             :uberjar {:aot :all}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "lean-discussion.core/mount-root"}
                        :compiler {:main lean-discussion.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :foreign-libs [{:file "resources/public/js/node_modules/react-countdown-clock/build/react-countdown-clock.js"
                                                   :provides ["ReactCountdownClock"]}]
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
                        :jar true
                        :compiler {:main lean-discussion.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :closure-defines {"goog.DEBUG" false}
                                   :foreign-libs [{:file "resources/public/js/node_modules/react-countdown-clock/build/react-countdown-clock.js"
                                                   :provides ["ReactCountdownClock"]}]
                                   :pretty-print false}}]
              :test-commands {"unit" ["phantomjs"
                                      "resources/test/phantom/runner.js"
                                      "resources/test/test.html"]}}
  :doo {:build "test"}
  :npm {:root "resources/public/js"
        :dependencies [[react-countdown-clock "1.0.5"]]}
  :main lean-discussion.server
  :prep-tasks [["cljsbuild" "once" "min"] "compile"])

