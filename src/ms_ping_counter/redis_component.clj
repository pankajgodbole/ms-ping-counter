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
        (println "redis-component/RedisComponent/start:\nRedisComponent:" this)
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
  (println "redis-component/create-redis-instance:\nuri:" uri)
  (map->RedisComponent {:uri uri}))

;;
;; Functions for interacting with our Redis component
;;
(defn ping
  "Check that Redis connection is active"
  [redis]
  (println "redis-component/ping:\n redis:" redis)
  (carmine/wcar (:connection redis) (carmine/ping)))

(defn getKey
  "Retrieve count for a key in Redis DB."
  [redis key]
  (println "redis-component/getKey:\n redis:" redis)
  (carmine/wcar (:connection redis) (carmine/get key)))

(defn incr
  "Increment count for a key in Redis DB."
  [redis key]
  (println "redis-component/incr:\n redis:" redis)
  (carmine/wcar (:connection redis) (carmine/incr key)))