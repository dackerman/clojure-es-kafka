(ns kafka.producer
  (:require [clj-kafka.core :as core]
            [clj-kafka.producer :as producer]
            [clj-kafka.consumer.zk :as zk]))

;(brokers {"zookeeper.connect" "127.0.0.1:2181"})

(def ^:private consumer-config {"zookeeper.connect" "localhost:2181"
                                "group.id" "clj-kafka.consumer"
                                "auto.offset.reset" "smallest"
                                "auto.commit.enable" "true"})

(def ^:private producer-config {"metadata.broker.list" "localhost:9092"
                                "serializer.class" "kafka.serializer.DefaultEncoder"
                                "partitioner.class" "kafka.producer.DefaultPartitioner"})

(def ^:private p (producer/producer producer-config))

(defn read-events [consumer-name callback]
  (let [c (zk/consumer (assoc consumer-config "group.id" consumer-name))]
    (doseq [message (zk/messages c "events")]
      (callback message))))

(defn publish-event [msg-key msg]
  (producer/send-message p
    (producer/message "events" (.getBytes msg-key) (.getBytes (pr-str msg)))))