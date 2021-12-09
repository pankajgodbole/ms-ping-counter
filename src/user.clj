;;
;; user.clj
;;

(ns user
  (:require [ms-ping-counter.core            :as core]
            [ms-ping-counter.http-server     :as http-server]
            [ms-ping-counter.http-handlers   :as http-handlers]
            [ms-ping-counter.redis-component :as redis-component]

            [ring.mock.request               :as    ring-mock-request]))

(def redis-url (core/get-redis-url))

(defn get-redis-instance
  []
  (redis-component/create-redis-instance redis-url))

(defn get-ip-address
  []
  (:remote-addr (-> (ring-mock-request/request :get "/"))))

(def redis-instance #'get-redis-instance)

(def ip-address #'get-ip-address)

(redis-component/ping (redis-instance))

(redis-component/get-val-by-key (redis-instance)
                                (ip-address))

(redis-component/increment-count (redis-instance)
                                 (ip-address))