(ns application.commands
  (:require [kafka.producer :as kafka]
            [domain.cases :as cases]
            [domain.culprits :as culprits]
            [eventstore.eventstore :as es]))

(defn get-case [case-key]
  (es/rebuild case-key cases/apply-event))

(defn get-culprit [culprit-name]
  (es/rebuild culprit-name culprits/apply-event))

(defn save-and-publish [key domain-event]
  "Saves the event to the event store and publishes it to the kafka queue for projections"
  (let [event (es/save-event key domain-event)]
    (kafka/publish-event key event)))


(defn refer-case [claim-number assignee]
  "Refers a suspect with the given claim number and assignee"
  (let [case-referred-event (cases/refer-suspect claim-number assignee)
        case-key (:case-key case-referred-event)]
    (save-and-publish case-key case-referred-event)
    (get-case case-key)))

(defn create-culprit [culprit-name age dob]
  "Creates a new culprit with the given name"
  (save-and-publish culprit-name (culprits/create culprit-name age dob))
  (get-culprit culprit-name))

(defn resolve-case [case-key culprit-name]
  "Resolves the case, blaming the given culprit"
  (let [case (get-case case-key)
        culprit (get-culprit culprit-name)
        resolution (domain.cases.Resolution. nil culprit)]
    (save-and-publish case-key (cases/resolve case resolution))
    (get-case case-key)))

(defn close-case [case-key]
  "Closes a case with the given aggregate key"
  (let [case (get-case case-key)]
    (save-and-publish case-key (cases/close case))
    (get-case case-key)))