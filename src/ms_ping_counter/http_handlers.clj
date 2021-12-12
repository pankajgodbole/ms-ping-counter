;;
;; src/ms_ping_counter/http_handlers.clj
;;

(ns ms-ping-counter.http-handlers
  (:require [ms-ping-counter.redis-operations :as redis-operations]))

(defn root-handler
  ""
  []
  (println "http-handlers/root-handler: Welcome to the Ping Counter microservice!\n")
  "Welcome to the Ping Counter microservice!")

(defn hello-world-handler
  "Sanity check"
  []
  (println "http-handlers/hello-world-handler: Howdy!")
  "Howdy!")

(defn ping-handler
  "Checks whether our HTTP server can interface with Redis"
  [redis-component]
  (println "http-handlers/ping-handler: redis-component:\n"
           redis-component)
  (redis-operations/ping-redis redis-component))

(defn counter-handler
  "Increments the count of the times that this IP address has accessed the
   endpoint and, returns the count"
  [http-request redis-component]
  (println "http-handlers/counter-handler: http-request:\n"
           http-request)
  (println "http-handlers/counter-handler: redis-component:\n"
           redis-component)
  (let [ip-address     (:remote-addr http-request)
        ping-count     (redis-operations/get-val-by-key redis-component ip-address)]
    (println "http-handlers/counter-handler: ip-address:\n"
             ip-address)
    (println "http-handlers/counter-handler: ping-count:\n"
             ping-count)
    (println "http-handlers/counter-handler: (redis-operations/increment-ping-count redis-component ip-address):\n"
             ping-count)
    (str "Ping count: "
         (redis-operations/increment-ping-count redis-component ip-address))))