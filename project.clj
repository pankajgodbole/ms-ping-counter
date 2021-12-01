(defproject ms-ping-counter "0.1.0-SNAPSHOT"
  :description  "Simple Clojure microservice"
  :url          "https://github.com/tsully/clojure-for-js-devs"
  :dependencies [[org.clojure/clojure        "1.10.3"]
                 [com.stuartsierra/component "1.0.0"]
                 [com.taoensso/carmine       "2.20.0"]
                 [ring/ring-core             "1.8.0"]
                 [ring/ring-defaults         "0.3.2"]
                 [ring/ring-jetty-adapter    "1.8.0"]
                 [ring/ring-json             "0.5.0"]
                 [compojure                  "1.6.2" :exclusions [ring/ring-core]]
                 [ring/ring-mock             "0.4.0"]
                 [circleci/bond              "0.5.0"]
                 [clj-http-fake              "1.0.3"]]
  :main         ^:skip-aot ms-ping-counter.core
  :target-path  "target/%s"
  :profiles     {:uberjar {:aot :all
                           :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
                 :dev     {:dependencies [[ring/ring-mock]
                                          [circleci/bond       "0.5.0"]
                                          [lambdaisland/kaocha "1.0.829"]]}}
  :aliases      {"test-with-kaocha" ["run" "-m" "kaocha.runner"]})