(defproject lean-discussion "0.1.0-SNAPSHOT"

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "script"]

  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]

  :lein-tools-deps/config {:config-files [:install :user :project]}

  :plugins [[lein-tools-deps "0.4.3"]
            [lein-cljsbuild "1.1.4"]
            [lein-garden "0.2.8" :exclusions [org.apache.commons/commons-compress]]
            [lein-less "1.7.5"]]


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

  :profiles {:dev {:plugins [[lein-figwheel "0.5.18"]
                             [lein-doo "0.1.6"]
                             [lein-ancient "0.6.15"]]}
             :uberjar {:aot :all}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "lean-discussion.core/mount-root"}
                        :compiler {:main lean-discussion.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :foreign-libs [{:file "resources/public/js/node_modules/react-countdown-clock/build/react-countdown-clock.js"
                                                   :provides ["ReactCountdownClock"]}]
                                   :externs ["externs.js" "jquery/jquery.js" "jquery/jquery.ui.js"]
                                   :closure-defines {"goog.DEBUG" true
                                                     "re_frame.trace.trace_enabled_QMARK_" true
                                                     "clairvoyant.core.devmode" true}
                                   :asset-path "js/compiled/out"
                                   :preloads [devtools.preload day8.re-frame-10x.preload]
                                   ;:external-config {:devtools/config {:features-to-install :all}}
                                   :optimizations :none
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs"]
                        :compiler {:output-to "resources/public/js/compiled/test.js"
                                   :output-dir "resources/public/js/compiled/out/test"
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
                                   :output-dir "resources/public/js/compiled/out/min"
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

