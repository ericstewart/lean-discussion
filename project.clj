(defproject lean-discussion "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.0"]
                 [re-frame "0.8.0"] ;; :exclusions [cljsjs/react]
                 [re-com "0.9.0"]
                 [secretary "1.2.3"]
                 [garden "1.3.2"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [ring "1.4.0"]
                 [binaryage/devtools "0.7.0"]
                 [alandipert/storage-atom "2.0.1"]
                 [re-frisk "0.2.2"]
                 [org.clojars.stumitchell/clairvoyant "0.2.0"]
                 [day8.re-frame/undo "0.3.2"]
                 [day8/re-frame-tracer "0.1.1-SNAPSHOT"]
                 [cljsjs/jquery "2.2.4-0"]
                 [cljsjs/jquery-ui "1.11.4-0"]
                 [cljsjs/semantic-ui "2.2.4-0"]]


  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "script"]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-garden "0.2.8" :exclusions [org.apache.commons/commons-compress]]
            [lein-less "1.7.5"]]
            ;[lein-npm "0.6.2"]]


  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css/compiled"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler lean-discussion.handler/dev-handler
             :open-file-command "open-in-intellij"}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :garden {:builds [{:id "screen"
                     :source-paths ["src/clj"]
                     :stylesheet lean-discussion.css/screen
                     :compiler {:output-to "resources/public/css/screen.css"
                                :pretty-print? true}}]}

  :profiles {:dev {:dependencies [
                                  [figwheel-sidecar "0.5.4-5" :exclusions [ring/ring-core ring/ring-codec commons-io joda-time clj-time org.clojure/clojurescript]]
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
                                   :externs ["externs.js"]
                                   :foreign-libs [{:file "resources/public/js/node_modules/react-countdown-clock/build/react-countdown-clock.js"
                                                   :provides ["ReactCountdownClock"]}]
                                   :closure-defines {"goog.DEBUG" true
                                                     "clairvoyant.core.devmode" true}
                                   :asset-path "js/compiled/out"
                                   ;:preloads [devtools.preload]
                                   ;:external-config {:devtools/config {:features-to-install :all}}
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs"]
                        :compiler {:output-to "resources/public/js/compiled/test.js"
                                   :main lean-discussion.runner
                                   :externs ["externs.js"]
                                   :foreign-libs [{:file "resources/public/js/node_modules/react-countdown-clock/build/react-countdown-clock.js"
                                                   :provides ["ReactCountdownClock"]}]
                                   :optimizations :none}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :jar true
                        :compiler {:main lean-discussion.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :externs ["externs.js"]
                                   :closure-defines {"goog.DEBUG" false}
                                   :foreign-libs [{:file "resources/public/js/node_modules/react-countdown-clock/build/react-countdown-clock.js"
                                                   :provides ["ReactCountdownClock"]}]
                                   :pretty-print false}}]
              :test-commands {"unit" ["phantomjs"
                                      "resources/test/phantom/runner.js"
                                      "resources/test/test.html"]}}
  :doo {:build "test"}
  :npm {:root "resources/public/js"
        :dependencies [
                       ;[react "15.1.0"]
                       ;[react-dom "15.1.0"]
                       [react-countdown-clock "1.0.5"]]
                       ;[webpack "^1.13.1"]]
                       ;[semantic-ui "2.2.4"]]
        :package {:scripts {:watch "webpack -d --watch"}}}
                            ;:build "webpack -p"
                            ;:dev "webpack --output-filename webpack-deps.js"
                            ;:min "webpack --optimize-minimize --output-filename webpack-deps.min.js"
                            ;:postinstall "npm run dev && npm run min"}}}

  :main lean-discussion.server
  ;:aot lean-discussion.server
  ;:uberjar-name "lean-discussion"
  :prep-tasks [["cljsbuild" "once" "min"] "compile"])

