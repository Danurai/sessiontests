(defproject sessiontests "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"
  
  :main  sessiontests.system  
  
  :jar-name     "session.jar"
  :uberjar-name "session-standalone.jar"

  :dependencies [[org.clojure/clojure "1.9.0-beta4"]
               [org.clojure/clojurescript "1.9.946"]
               [http-kit "2.2.0"]
               [com.stuartsierra/component "0.3.2"]
               [compojure "1.6.0"]
               [jarohen/chord "0.8.1"]
               [org.clojure/core.async  "0.3.443"]
               [ring/ring-defaults "0.3.1"]
               [reagent "0.7.0"]
               [reagent-utils "0.2.1"]]

  :plugins [[lein-figwheel "0.5.14"]
           [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src/clj"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs"]
                :figwheel true
                :compiler {:main sessiontests.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/sessiontests.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           ;; To console.log CLJS data-structures make sure you enable devtools in Chrome
                           ;; https://github.com/binaryage/cljs-devtools
                           :preloads [devtools.preload]}}
               ;; This next build is a compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id "min"
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/compiled/sessiontests.js"
                           :main sessiontests.core
                           :externs ["resources/public/js/libs/externs.js"] ;; var tooltip = {}; prevents .tooltip from being munged on cljsbuild http://lukevanderhart.com/2011/09/30/using-javascript-and-clojurescript.html
                           :optimizations :advanced ;; :none :whitespace :simple :advanced - :whitespace required to prevent munging e.g. .tooltip() .draggable() https://developers.google.com/closure/compiler/docs/compilation_levels
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]} ;; watch and update CSS

  ;; Setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles {:uberjar {:aot :all
                     :source-paths ["src"]
                     :prep-tasks ["compile" ["cljsbuild" "once" "min"]]}
            :dev {:dependencies [[reloaded.repl "0.2.4"]
                              [figwheel-sidecar "0.5.14"]
                              [binaryage/devtools "0.9.4"]
                              [com.cemerick/piggieback "0.2.2"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src/clj" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
