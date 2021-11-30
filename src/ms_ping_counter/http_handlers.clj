;;
;; src/ms_ping_counter/http_handlers.clj
;;

(ns ms-ping-counter.http-handlers
  (:require [ms-ping-counter.redis-component :as redis]))

(defn hello-world-handler
  "Sanity check"
  []
  "ms-ping-counter.http-handlers/ping-handler:\nHowdy!")

(defn ping-handler
  "Checks whether our HTTP server can interface with Redis"
  [redis-component]
  (println "ms-ping-counter.http-handlers/ping-handler:\n Handling ping request")
  (redis/ping redis-component))

(defn counter-handler
  "Increments the count of the times that this IP address has accessed the endpoint
   and, returns the count"
  [http-request redis-component]
  (let [ip-address (:remote-addr http-request)
        counter    (redis/getKey redis-component ip-address)]
    (redis/incr redis-component ip-address)
    (str "Counter: " counter)))