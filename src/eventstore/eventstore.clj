(ns eventstore.eventstore)

(def ^:private events (atom []))

(defn read-events []
  @events)

(defrecord Event [id aggregate-key aggregate-version type payload])

(defn aggregate-events [aggregate-key events]
  (filter #(= (:aggregate-key %) aggregate-key) events))

(defn ^:private create-event [key domain-event last-event-id last-aggregate-version]
  (Event.
    (inc last-event-id)
    key
    (inc last-aggregate-version)
    (class domain-event)
    domain-event))


(defn save-event [key domain-event]
  (dosync
    (let [last-event-id (or (:id (last @events)) -1)
          last-aggregate-version (or (:aggregate-version (last (aggregate-events key @events))) -1)
          new-event (create-event key domain-event last-event-id last-aggregate-version)]
      (swap! events conj new-event)
      new-event)))

(defn payload [events]
  (map #(:payload %) events))

(defn rebuild-entity [events fn]
  (reduce fn nil events))

(defn rebuild [aggregate-key fn]
  (rebuild-entity (payload (aggregate-events aggregate-key @events)) fn))


