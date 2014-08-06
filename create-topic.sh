cd ~/programs/kafka_2.10-0.8.1.1/
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1000 --topic events

