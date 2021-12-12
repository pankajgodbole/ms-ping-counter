;;
;; redis.clj
;;
(ns ms-ping-counter.redis-component
  (:gen-class)
  (:require [com.stuartsierra.component :as component]))

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