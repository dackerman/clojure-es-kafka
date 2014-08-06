(ns eventsourcing-kafka.core
  (:gen-class)
  (:require clojure.pprint
            [kafka.producer :as kafka]
            [domain.cases :as cases]
            [eventstore.eventstore :as es]
            [application.commands :as commands]))


(defn print-entity [aggregate-key]
  "Reads an entity from the event store and prints it"
  (println (pr-str (es/rebuild aggregate-key cases/apply-event))))


(defn print-events []
  "Prints all the events in the event store"
  (doseq [e (es/read-events)]
    (println (pr-str e))))

(defn try-run [command params]
  "Runs the command, and prints exceptions instead of crashing the program."
  (try
    (println (apply command params))
    (catch Throwable e (println (.getMessage e)))))

(defn project [projection event projection-fn]
  "Updates the projection atom by applying the projection-fn to the event"
  (swap! projection #(projection-fn event (:payload event) %)))


(defn consume-events [consumer-name]
  "Listens for kafka events and applies the cases/referrals-by-status projection to it"
  (let [projection (atom {})]
    (kafka/read-events consumer-name
      (fn [event]
        (let [event-str (apply str (map char (:value event)))
              orig-event (read-string event-str)]
          (println "\n------------------------------------\n")
          (project projection orig-event cases/project-referrals-by-status)
          (println (clojure.pprint/pprint @projection)))))))


(defn produce-events []
  "Listens for user input line by line, applying the given command."
  (do
    (println "Ready for input")
    (while true
      (let [[command & params] (clojure.string/split (read-line) #"\s+")]
        (condp = command
          "ReferCase" (try-run commands/refer-case params)
          "CloseCase" (try-run commands/close-case params)
          "ResolveCase" (try-run commands/resolve-case params)
          "NewCulprit" (try-run commands/create-culprit params)

          "print" (apply print-entity params)
          "events" (print-events)
          "exit" (System/exit 0)
          )))))

(defn -main [& args]
  "Run the program with either consume or produce as an argument.
   'produce' starts up a kafka producer that posts events to the queue in the
   topic 'events'.
   'consume' starts a kafka consumer that runs a projection on what is output
   by the producer."
  (let [mode (first args)]
    (condp = mode
      "consume" (apply consume-events (rest args))
      "produce" (produce-events))))
