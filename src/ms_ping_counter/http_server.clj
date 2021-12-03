;;
;; ms-ping-counter.http-server.clj
;;

(ns ms-ping-counter.http-server
   (:gen-class)
   (:require
      [com.stuartsierra.component    :as component]
      [ring.adapter.jetty            :as ring-jetty]
      [ring.middleware.defaults      :as ring-defaults]
      [ring.middleware.json          :as ring-json]
      [compojure.core                :refer [GET routes]]
      [compojure.route               :as compojure-route]
      [ms-ping-counter.http-handlers :as http-handlers]))

(defn app-routes
   "Creates the Ring http-handler that defines the routes and route handlers"
   [redis-component]
  (prn "http-server/app-routes:\nredis-component:\n"
       redis-component)
  (routes
    (GET "/" []
      (http-handlers/root-handler))
    (GET "/hello-world" []
      (http-handlers/hello-world-handler))
    (GET "/ping" []
      (http-handlers/ping-handler redis-component))
    (GET "/counter" http-request
      (http-handlers/counter-handler http-request redis-component))
    (compojure-route/not-found "http-server/app-routes:\nRoute was not found.")))

(defn start-server
   "Start the HTTP server with our routes and middlewares"
   ([host port redis-component]
    (prn "http-server/start-server:\nhost, port, redis-component:\n"
         host port redis-component)
    (-> (app-routes redis-component)
        ;; Turn map response bodies into JSON with the correct headers
        (ring-json/wrap-json-response)
        ;; Turn JSON into a map
        (ring-json/wrap-json-body {:keywords? true})
        ;; Parse query strings and set default response headers
        (ring-defaults/wrap-defaults ring-defaults/api-defaults)
        ;; Start the server
        (ring-jetty/run-jetty {:host  host
                               :port  port
                               :join? false}))))

(defrecord HttpServer
  [host port]
  component/Lifecycle
  (start [this]
    (prn "http-server/HttpServer/start:\nhost, port, this:\n"
         host port this)
    (if (:server this)
       this
       (let [http-server (start-server host port (:redis this))]
         (assoc this :server http-server))))
  (stop [this]
    (prn "http-server/HttpServer/stop:\nhost, port, this:\n"
         host port this)
   (when-let [http-server (:server this)]
      (.stop http-server))
   (dissoc this :server)))

(defn create-new-server
  [host port]
  (prn "http-server/create-new-server:\nhost, port:"
       host port)
  (->HttpServer host port))