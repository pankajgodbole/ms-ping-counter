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
  (testing "hello-world-handler"
    (is (= (http-handlers/hello-world-handler)
           "ms-ping-counter.http-handlers/ping-handler:\nHowdy!"))))

(deftest counter-handler-test
  (testing "counter handler"
    (let [http-response "Counter: 44"
          mock-request  (-> (ring-mock-request/request :get "/counter"))]
      (bond-james/with-stub!
        [[redis-component/getKey (constantly 44)]
         [redis-component/incr (constantly nil)]]
        (is (= (http-handlers/counter-handler mock-request {})
               http-response))))))