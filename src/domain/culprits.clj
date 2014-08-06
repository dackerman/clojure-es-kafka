(ns domain.culprits)

(defrecord CulpritCreatedEvent [culprit-name age dob])

(defrecord Culprit [culprit-name age date-of-birth])

(defn create [culprit-name age dob]
  "Creates a culprit, with a name, age and date of birth"
  (CulpritCreatedEvent. culprit-name age dob))

(defn apply-event [culprit-state e]
  (condp = (class e)
    CulpritCreatedEvent (Culprit. (:culprit-name e) (:age e) (:dob e))))

