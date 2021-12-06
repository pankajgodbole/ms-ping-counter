;;
;; src/ms_ping_counter/http_handlers.clj
;;

(ns ms-ping-counter.http-handlers
  (:require [ms-ping-counter.redis-component :as redis-component]))

(defn root-handler
  ""
  []
  (println "http-handlers/root-handler:\nWelcome to the Ping Counter microservice!\n")
  "Welcome to the Ping Counter microservice!")

(defn hello-world-handler
  "Sanity check"
  []
  (println "http-handlers/hello-world-handler:\nHowdy!")
  "Howdy!")

(defn ping-handler
  "Checks whether our HTTP server can interface with Redis"
  [redis-component]
  (println "http-handlers/ping-handler:\nredis-component:\n"
           redis-component)
  (redis-component/ping redis-component))

(defn counter-handler
  "Increments the count of the times that this IP address has accessed the
   endpoint and, returns the count"
  [http-request redis-component]
  (println "http-handlers/counter-handler:\nhttp-request:\n"
           http-request)
  (println "http-handlers/counter-handler:\nredis-component:\n"
           redis-component)
  (let [ip-address (:remote-addr http-request)
        counter    (redis-component/get-val-by-key redis-component ip-address)]
    (redis-component/increment-count redis-component ip-address)
    (str "Counter: " counter)))