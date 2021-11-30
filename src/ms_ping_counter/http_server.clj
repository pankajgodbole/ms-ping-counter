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
   (routes
      (GET "/hello-world"
           []
           (http-handlers/hello-world-handler))
      (GET "/ping"
           []
           (http-handlers/ping-handler redis-component))
      (GET "/counter"
           http-request
           (http-handlers/counter-handler http-request redis-component))
      (compojure.route/not-found "Not found")))

(defn start-server
   "Start the HTTP server with our routes and middlewares"
   ([host port redis-component]
    (println "ms-ping-counter.ms-ping-counter.http-server/start-server: Executing...")
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
   (if (:server this)
     this
     (let [http-server (start-server host port (:redis this))]
       (assoc this :server http-server))))
  (stop [this]
   (when-let [http-server (:server this)]
      (.stop http-server))
   (dissoc this :server)))

(defn create-new-server
  [host port]
  (->HttpServer host port))