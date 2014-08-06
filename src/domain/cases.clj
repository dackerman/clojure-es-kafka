(ns domain.cases)

(defrecord CaseReferredEvent [case-key claim-number assignee])
(defrecord CaseClosedEvent [])
(defrecord CaseResolvedEvent [assignee culprit-name])

(defrecord Suspect [claim-number])
(defrecord Resolution [assignee culprit])

(defrecord Case [case-key primary-entity assignee status])

(defn close [case]
  "Closes a case"
  (CaseClosedEvent.))

(defn is-closed [case]
  "Returns whether a case is closed"
  (= :closed (:status case)))

(defn resolve [case resolution]
  "Resolves a case with the given resolution, as long as it isn't already closed"
  (if (is-closed case)
    (throw (RuntimeException. "Case is already closed"))
    (CaseResolvedEvent. (:assignee resolution) (:culprit-name (:culprit resolution)))))


(defn refer-suspect [claim-number assignee-email]
  "Refers a suspect into a case, and assigns it to the given email"
  (CaseReferredEvent. (str (java.util.UUID/randomUUID)) claim-number assignee-email))


(defn apply-event [case-state e]
  "Builds up the state of a case based on its events"
  (condp = (class e)
    CaseReferredEvent (Case. (:case-key e) (:claim-number e) (:assignee e) :open)
    CaseClosedEvent (assoc case-state :status :closed)
    CaseResolvedEvent (merge case-state {:resolution {:culprit (:culprit-name e),
                                                :by (:assignee e)}
                                    :status :closed})))


(defn project-referrals-by-status [orig-event event referrals-by-name]
  "Runs a projection that keeps cases keyed on status in memory"
  (condp = (class event)
    CaseReferredEvent (update-in referrals-by-name [:open] conj (:case-key event))
    CaseClosedEvent (-> referrals-by-name
                      (update-in [:closed] conj (:aggregate-key orig-event))
                      (update-in [:open] (fn [rbn] (remove #(= % (:aggregate-key orig-event)) rbn))))
    referrals-by-name))