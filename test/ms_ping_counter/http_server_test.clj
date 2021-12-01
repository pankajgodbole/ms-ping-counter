;;
;; http_server_test.clj
;;
;;
;;
;;

(ns ms-ping-counter.http-server-test
  (:require [clojure.test              :refer [deftest is testing use-fixtures]]
            [clj-http.client           :as http-client]
            [ms-ping-counter.core-test :as core-test]))