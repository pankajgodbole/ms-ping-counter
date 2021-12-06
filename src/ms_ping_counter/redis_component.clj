;;
;; redis.clj
;;
(ns ms-ping-counter.redis-component
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [taoensso.carmine           :as carmine]))

(defrecord RedisComponent
  [uri connection]

  ;; Implementation of the Lifecycle type in component
  component/Lifecycle
  (start
    [this]
    (println "redis-component/RedisComponent/start: Starting the Redis component...")
    ;; If there is already a connection return the instance of the Redis class,
    ;; otherwise associate the 'connection' property of this defrecord with
    ;; a map representing the Redis connection.
    (if (:connection this)
      this
      (do
        (println "redis-component/RedisComponent/start:\nRedisComponent:\n"
                 this)
        (assoc this :connection {:pool {}
                                 :spec {:uri uri}}))))
  (stop
    [this]
    (println "redis-component/RedisComponent/stop: Stopping the Redis component...")
    (println "redis-component/RedisComponent/stop:\nRedisComponent:\n"
             this)
    (if (:connection this)
     (do
       (assoc this :connection nil))
     this)))

(defn create-redis-instance
  [uri]
  (println "redis-component/create-redis-instance:\nuri:\n"
           uri)
  (map->RedisComponent {:uri uri}))

;;
;; Functions for interacting with our Redis component
;;
(defn ping
  "Check that Redis connection is active"
  [redis-component]
  (println "redis-component/ping:\n redis-component:\n"
           redis-component)
  (carmine/wcar (:connection redis-component) (carmine/ping)))

(defn get-val-by-key
  "Retrieve count for a key in Redis DB."
  [redis-component key]
  (println "redis-component/get-val-by-key:\nredis-component, key:\n"
           redis-component key)
  (carmine/wcar (:connection redis-component) (carmine/get key)))

(defn increment-count
  "Increment count for a key in Redis DB."
  [redis-component key]
  (println "redis-component/increment-count:\nredis-component, key:\n"
           redis-component key)
  (carmine/wcar (:connection redis-component) (carmine/incr key)))