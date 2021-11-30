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
    ;; If there is already a connection return the instance of the Redis class,
    ;; otherwise associate the 'connection' property of this defrecord with
    ;; a map representing the Redis connection.
    (if (:connection this)
      this
      (do
        (println "Starting the Redis component")
        (println "Redis connection URI:" this)
        (assoc this :connection {:pool {}
                                 :spec {:uri uri}}))))
  (stop
    [this]
    (if (:connection this)
     (do
       (println "Stopping this Redis component")
       (assoc this :connection nil))
     this)))

(defn create-redis-instance
  [uri]
  (map->RedisComponent {:uri uri}))

;;
;; Functions for interacting with our Redis component
;;
(defn ping
  "Check that Redis connection is active"
  [redis]
  (println "ms-ping-counter.redis-component/ping: \nredis:" redis)
  (carmine/wcar (:connection redis) (carmine/ping)))

(defn getKey
  "Retrieve count for a key in Redis DB."
  [redis key]
  (carmine/wcar (:connection redis) (carmine/get key)))

(defn incr
  "Increment count for a key in Redis DB."
  [redis key]
  (carmine/wcar (:connection redis) (carmine/incr key)))