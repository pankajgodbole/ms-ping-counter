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
            [ms-ping-counter.redis-component :as redis]
            [ms-ping-counter.http-server     :as http-server]))

(def app-host "0.0.0.0")

(def app-port
  "Heroku dynamically assigns your app a port, so you can't set the port to a
   fixed number. Heroku adds the port to the env, so you can pull it from there.
   (Ref: https://stackoverflow.com/a/15693371/189785)"
  (or (System/getenv "PORT")
      8080))

(defonce ^:dynamic *system* nil)

(defn main-system
  "Implements Component's Lifecycle protocol and creates a map of component dependencies"
  []
  (println "core/main-system:\napp-host, app-port:" app-host app-port)
  (component/system-map
    :redis       (redis/create-redis-instance "redis://localhost:6379")
    :http-server (component/using (http-server/create-new-server app-host
                                                                 app-port)
                                  [:redis])))

(defn start
  "Starts components of system in dependency order. Runs the SystemMap implementation for the
  Lifecycle protocol's 'start' function"
  [system]
  (println "core/start:\nsystem:" system)
  (try
    (component/start system)
    (catch Exception e
      (println "ms-ping-counter.core/start: Failed to start the system:\n"
               e))))

(defn stop
  "Stop the components of system in dependency order. Runs the SystemMap implementation for the
   Lifecycle protocol's 'start' function"
  [system]
  (println "core/stop:\nsystem:" system)
  (component/stop system)
  ;; Dynamically rebind *system* back to nil
  (alter-var-root #'*system* (constantly nil)))

(defn -main
  "The application's entry point"
  []
  (println "core/-main:\nHello, World!")
  (let [main-system (start (main-system))]
    ;; Dynamically rebind *system* to the newly created SystemMap instance
    (alter-var-root #'*system* (constantly main-system))
    ;; Create a hook that stops the component 'system' in a controlled manner
    ;; before the JVM completely shuts down
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. (partial stop main-system)))))