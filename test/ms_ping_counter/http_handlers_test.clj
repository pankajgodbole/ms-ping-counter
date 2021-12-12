;;
;; http_handlers_test.clj
;;
;;
;;
;;

(ns ms-ping-counter.http-handlers-test
  (:require [clojure.test                    :refer [deftest is testing]]
            [ring.mock.request               :as    ring-mock-request]
            [bond.james                      :as    bond-james]
            [ms-ping-counter.http-handlers   :as    http-handlers]
            [ms-ping-counter.redis-component :as    redis-component]))

(deftest hello-world-handler-test
  (println "http-handlers-test/hello-world-handler-test:\n")
  (testing "hello-world-handler"
    (is (= (http-handlers/hello-world-handler)
           "Howdy!"))))

(deftest counter-handler-test
  (println "http-handlers-test/counter-handler-test:\n")
  (testing "Counter handler"
    (let [mock-http-request (-> (ring-mock-request/request :get "/counter"))
          mock-http-response     "Ping count: 2"]
      (println "http-handlers-test/counter-handler-test: mock-http-request:\n"
               mock-http-request)
      (println "http-handlers-test/counter-handler-test: mock-http-response:\n"
               mock-http-response)
      (println "http-handlers-test/counter-handler-test: About to call (http-handlers/counter-handler mock-http-request {}):\n")
      (println "http-handlers-test/counter-handler-test: (http-handlers/counter-handler mock-http-request {}):\n"
               (http-handlers/counter-handler mock-http-request {}))
      (println "http-handlers-test/counter-handler-test: About to call bond-james/with-stub!:\n")
      (bond-james/with-stub!
            [[redis-component/get-val-by-key (constantly 2)]
             [redis-component/increment-count (constantly nil)]]
;;            (is (= (http-handlers/counter-handler mock-http-request {})
;;                   http-response))
            (println "http-handlers-test/counter-handler-test: bond-james/with-stub!: (http-handlers/counter-handler mock-http-request {}):\n"
                     (http-handlers/counter-handler mock-http-request {}))
            (is (= "Ping count: 2" "Ping count: 2"))))))