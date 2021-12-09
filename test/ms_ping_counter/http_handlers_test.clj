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
    (let [http-response     "Ping count: 44"
          mock-http-request (-> (ring-mock-request/request :get "/counter"))]
      (println "http-handlers-test/counter-handler-test:\nmock-request:\n"
               mock-http-request)
      (bond-james/with-stub!
            [[redis-component/get-val-by-key (constantly 44)]
             [redis-component/increment-count (constantly nil)]]
            (is (= (http-handlers/counter-handler mock-http-request {})
                   http-response))))))