;;
;; redis_operations.clj
;;
;; Functions for interacting with our Redis component
;;

(ns ms-ping-counter.redis-operations
  (:require [taoensso.carmine           :as carmine]
            [ms-ping-counter.redis-component :as redis-component]))

(defn create-redis-instance
  [uri]
  (println "redis-operations/create-redis-instance: uri:\n"
           uri)
  (redis-component/map->RedisComponent {:uri uri}))

(defn ping-redis
  "Check that Redis connection is active"
  [redis-component]
  (println "redis-operations/ping-redis: redis-component:\n"
           redis-component)
  (let [redis-ping-result (carmine/wcar (:connection redis-component) (carmine/ping))]
    (println "redis-operations/ping-redis: (carmine/wcar (:connection redis-component) (carmine/ping)):\n"
             redis-ping-result)
    redis-ping-result))

(defn get-val-by-key
  "Retrieve count for a key in Redis DB."
  [redis-component key]
  ;(println "redis-operations/get-val-by-key: redis-component:\n"
  ;         redis-component)
  ;(println "redis-operations/get-val-by-key: key:\n"
  ;         key)
  (let [ip-address-val (carmine/wcar (:connection redis-component) (carmine/get key))]
    (println "redis-operations/get-val-by-key: (carmine/wcar (:connection redis-component) (carmine/get key)):\n"
             ip-address-val)
    ip-address-val))

(defn increment-ping-count
  "Increment count for an IP address stored as a key in Redis DB."
  [redis-component key]
  ;(println "redis-operations/increment-ping-count: redis-component:\n"
  ;         redis-component)
  ;(println "redis-operations/increment-ping-count: key:\n"
  ;         key)
  (let [incr-ip-address-val (carmine/wcar (:connection redis-component) (carmine/incr key))]
    (println "redis-operations/increment-ping-count: (carmine/wcar (:connection redis-component) (carmine/incr key)):\n"
             incr-ip-address-val)
    incr-ip-address-val))