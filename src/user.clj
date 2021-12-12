;;
;; user.clj
;;

(ns user
  (:require [ms-ping-counter.core            :as core]
            [ms-ping-counter.redis-operations :as redis-operations]

            [ring.mock.request               :as    ring-mock-request]))

(def redis-url (core/get-redis-url))

(defn get-redis-instance
  []
  (redis-operations/create-redis-instance redis-url))

(defn get-ip-address
  []
  (:remote-addr (-> (ring-mock-request/request :get "/"))))

(def redis-instance #'get-redis-instance)

(def ip-address #'get-ip-address)

(redis-operations/ping-redis (redis-instance))

(redis-operations/get-val-by-key (redis-instance)
                                (ip-address))

(redis-operations/increment-ping-count (redis-instance)
                                 (ip-address))