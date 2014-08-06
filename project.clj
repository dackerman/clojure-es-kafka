(defproject eventsourcing-kafka "0.1.0-SNAPSHOT"
  :description "Toy implementation of Event Sourcing with Clojure and Kafka"
  :url "https://github.com/dackerman/clojure-es-kafka"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                  [org.clojure/clojure "1.6.0"]
                  [clj-kafka "0.2.6-0.8"]
                  [org.clojure/core.match "0.2.1"]]
  :main ^:skip-aot eventsourcing-kafka.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
