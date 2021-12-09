;;
;; user.clj
;;

(ns user
  (:require [ms-ping-counter.core            :as core]
            [ms-ping-counter.http-server     :as http-server]
            [ms-ping-counter.http-handlers   :as http-handlers]
            [ms-ping-counter.redis-component :as redis-component]

            [ring.mock.request               :as    ring-mock-request]))

(declare get-redis-instance
         get-ip-address
         get-redis-url
         redis-url
         get-http-port
         http-host
         ^:dynamic *system*
         main-system)

(def redis-url (core/get-redis-url))

(def redis-instance #'get-redis-instance)

(def ip-address #'get-ip-address)

(defn get-redis-instance
  []
  (redis-component/create-redis-instance redis-url))

(defn get-ip-address
  []
  (:remote-addr (-> (ring-mock-request/request :get "/"))))

(redis-component/ping (get-redis-instance))

(redis-component/get-val-by-key (get-redis-instance)
                                (get-ip-address))

(redis-component/increment-count (get-redis-instance)
                                 (get-ip-address))