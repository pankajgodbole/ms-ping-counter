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
        (println "redis-component/RedisComponent/start: RedisComponent:\n"
                 this)
        (assoc this :connection {:pool {}
                                 :spec {:uri uri}}))))
  (stop
    [this]
    (println "redis-component/RedisComponent/stop: Stopping the Redis component...")
    (println "redis-component/RedisComponent/stop: RedisComponent:\n"
             this)
    (if (:connection this)
     (do
       (assoc this :connection nil))
     this)))

(defn create-redis-instance
  [uri]
  (println "redis-component/create-redis-instance: uri:\n"
           uri)
  (map->RedisComponent {:uri uri}))

;;
;; Functions for interacting with our Redis component
;;
(defn ping
  "Check that Redis connection is active"
  [redis-component]
  (println "redis-component/ping: redis-component:\n"
           redis-component)
  (let [redis-ping-result (carmine/wcar (:connection redis-component) (carmine/ping))]
    (println "redis-component/ping: (carmine/wcar (:connection redis-component) (carmine/ping)):\n"
             redis-ping-result)
    redis-ping-result))

(defn get-val-by-key
   "Retrieve count for a key in Redis DB."
   [redis-component key]
   ;(println "redis-component/get-val-by-key: redis-component:\n"
   ;         redis-component)
   ;(println "redis-component/get-val-by-key: key:\n"
   ;         key)
   (let [ip-address-val (carmine/wcar (:connection redis-component) (carmine/get key))]
     (println "redis-component/get-val-by-key: (carmine/wcar (:connection redis-component) (carmine/get key)):\n"
              ip-address-val)
     ip-address-val))

(defn increment-count
  "Increment count for a key in Redis DB."
  [redis-component key]
  ;(println "redis-component/increment-count: redis-component:\n"
  ;         redis-component)
  ;(println "redis-component/increment-count: key:\n"
  ;         key)
  (let [incr-ip-address-val (carmine/wcar (:connection redis-component) (carmine/incr key))]
    (println "redis-component/increment-count: (carmine/wcar (:connection redis-component) (carmine/incr key)):\n"
              incr-ip-address-val)
    incr-ip-address-val))