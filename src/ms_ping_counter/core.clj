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

(defonce ^:dynamic *system* nil)

(defn main-system
  "Implements Component's Lifecycle protocol and creates a map of component dependencies"
  []
  (component/system-map
    :redis       (redis/create-redis-instance "redis://localhost:6379")
    :http-server (component/using (http-server/create-new-server "0.0.0.0" 8080)
                                  [:redis])))

(defn start
  "Starts components of system in dependency order. Runs the SystemMap implementation for the
  Lifecycle protocol's 'start' function"
  [system]
  (try
    (component/start system)
    (catch Exception e
      (println "ms-ping-counter.core: Failed to start the system: " e))))

(defn stop
  "Stop the components of system in dependency order. Runs the SystemMap implementation for the
   Lifecycle protocol's 'start' function"
  [system]
  (component/stop system)
  ;; Dynamically rebind *system* back to nil
  (alter-var-root #'*system* (constantly nil)))

(defn -main
  "The application's entry point"
  []
  (println "Hello, World!")
  (let [main-system (start (main-system))]
    ;; Dynamically rebind *system* to the newly created SystemMap instance
    (alter-var-root #'*system* (constantly main-system))
    ;; Create a hook that stops the component 'system' in a controlled manner
    ;; before the JVM completely shuts down
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. (partial stop main-system)))))