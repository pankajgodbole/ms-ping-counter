;;
;; core_test.clj
;;
;; Defines the test “component map”
;;

(ns ms-ping-counter.core-test
  (:require [clojure.test                    :refer :all]
            [com.stuartsierra.component      :as component]
            [ms-ping-counter.http-server     :as http-server]
            [ms-ping-counter.redis-component :as redis-component]
            [ms-ping-counter.core            :refer :all]))

(def system nil)

(def ephemeral-port
  "To prevent bind conflicts between multiple test runs, bind to an
  ephemeral port that the OS chooses"
  0)

(defn- test-system
  "Creates a system map for use in integration tests."
  []
  (println "core-test/test-system:\n")
  (component/system-map
    :redis-connection (redis-component/create-redis-instance "redis://localhost:6379")
    :http-server      (component/using
                        (http-server/create-new-server "localhost" ephemeral-port)
                        {:redis-connection :redis-connection})))

(defn- setup-system
  "Rebind system variable to the system map"
  []
  (println "core-test/setup-system:\n")
  (alter-var-root #'system
                  (fn [_] (component/start (test-system)))))

(defn- tear-down-system
  "Stop each component in the system map, and the result to the system variable"
  []
  (println "core-test/tear-down-system:\n")
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn initilize-system
  "Create the test component system map, run the tests using the system map, and
  finally, tear down the system map"
  [test-fn]
  (println "core-test/initialize-system:\n")
  (setup-system)
  (test-fn)
  (tear-down-system))

(comment (deftest a-test
           (testing "FIXME, I fail."
             (is (= 0 1)))))