;;
;; core.clj
;;
;; Defines “component map” that will describe how our components, including Redis,
;; will interact with each other.
;;

(ns ms-ping-counter.core
  (:gen-class
    :main true)
  (:require [com.stuartsierra.component      :as component]
            [environ.core                    :as environ]
            [ms-ping-counter.redis-component :as redis]
            [ms-ping-counter.http-server     :as http-server]))

(declare get-redis-url
         redis-url
         get-http-port
         http-host
         ;;http-port
         ^:dynamic *system*
         main-system
         start
         stop)

(defn -main
  "The application's entry point"
  []
  (prn "core/-main:\nHello, World!")
  (let [main-system (start (main-system))]
    ;; Dynamically rebind *system* to the newly created SystemMap instance
    (alter-var-root #'*system* (constantly main-system))
    ;; Create a hook that stops the component 'system' in a controlled manner
    ;; before the JVM completely shuts down
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. (partial stop main-system)))))

(defn main-system
  "Implements Component's Lifecycle protocol and creates a map of component dependencies"
  []
  (let [redis-url (get-redis-url)
        http-port (get-http-port)]
    (prn "core/main-system:\nredis-url, http-host, http-port:\n"
         redis-url http-host http-port)
    (component/system-map
      :redis (redis/create-redis-instance redis-url)
      :http-server (component/using (http-server/create-new-server http-host
                                                                   http-port)
                                    [:redis]))))

(def redis-url #'get-redis-url)
;;(def http-host "localhost")
(def http-host "0.0.0.0")
(def http-port #'get-http-port)

(defn get-redis-url
  "For Heroku: fetch the config var REDIS_URL if present, or to run locally: return
   the default Redis URL otherwise
   Ref: https://devcenter.heroku.com/articles/getting-started-with-clojure#define-config-vars"
  []
  (let [redis-url (System/getenv "REDIS_URL")]
    (if-not (empty? redis-url)
      redis-url
      "redis://127.0.0.1:6379")))

(defn get-http-port
  "Heroku dynamically assigns your app a port, so you can't set the port to a
   fixed number. Heroku adds the port to the env, so you can pull it from there.
   (Ref: https://stackoverflow.com/a/15693371/189785)"
  []
  (let [port (System/getenv "PORT")]
    (if-not (empty? port)
      (java.lang.Long/parseLong port)
      8080)))

(defn start
  "Starts components of system in dependency order. Runs the SystemMap implementation for the
  Lifecycle protocol's 'start' function"
  [system]
  (try
    (prn "core/start:\nsystem:" system)
    (component/start system)
    (catch Exception e
      (prn "ms-ping-counter.core/start: Failed to start the system:\n"
           e))))

(defn stop
  "Stop the components of system in dependency order. Runs the SystemMap implementation for the
   Lifecycle protocol's 'start' function"
  [system]
  (prn "core/stop:\nsystem:" system)
  (component/stop system)
  ;; Dynamically rebind *system* back to nil
  (alter-var-root #'*system* (constantly nil)))

(defonce ^:dynamic *system* nil)