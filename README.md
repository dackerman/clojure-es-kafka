[ ![Codeship Status for dackerman/clojure-es-kafka](https://codeship.io/projects/5d262c50-1d84-0132-d52b-1e0ac5ac49fe/status)](https://codeship.io/projects/35412)
# clojure-es-kafka

Example code running an Event Sourcing implementation with a Kafka queue integrated.

## Installation

Download from https://github.com/dackerman/clojure-es-kafka.

## Usage

It's easiest to run from lein.  First you need to start up a kafka instance for this to work.  If you install kafka in `~/programs/kafka_2.10-0.8.1.1/` then this will "just work", otherwise you need to update the shell scripts manually to point to your instance.

First, start the zookeeper instance in one process:

    $ ./start-zk.sh

Then, start kafka:

    $ ./start-kafka.sh

After that, you will want to create the topic "events" if you don't have auto-create setup.

    $ ./create-topic.sh
    
Now you can run the program:

    $ lein run {produce, consume}

## Options

* `produce`: start a producer that takes commands as input and creates events based on a clojure domain model. It applies them to the in-memory event store and publishes those events to a kafka stream.
* `consume`: start a kafka consumer and transform the events to a projection as data comes in.

## Examples

This example starts up a producer and creates a "case", then "closes" it. Imagine "cases" as tasks to be assigned to a fraud investigator.

    [dackerman@stacktrace clojure-kafka]$ lein run produce
    Ready for input
    ReferCase 1234 dave
     #domain.cases.Case{:case-key 2b67ca4b-958c-417c-bde1-d8708536bc8b, :primary-entity 1234, :assignee dave, :status :open}
    CloseCase 2b67ca4b-958c-417c-bde1-d8708536bc8b
    #domain.cases.Case{:case-key 2b67ca4b-958c-417c-bde1-d8708536bc8b, :primary-entity 1234, :assignee dave, :status :closed}
    events
    #eventstore.eventstore.Event{:id 0, :aggregate-key "2b67ca4b-958c-417c-bde1-d8708536bc8b", :aggregate-version 0, :type domain.cases.CaseReferredEvent, :payload #domain.cases.CaseReferredEvent{:case-key "2b67ca4b-958c-417c-bde1-d8708536bc8b", :claim-number "1234", :assignee "dave"}}
    #eventstore.eventstore.Event{:id 1, :aggregate-key "2b67ca4b-958c-417c-bde1-d8708536bc8b", :aggregate-version 1, :type  domain.cases.CaseClosedEvent, :payload #domain.cases.CaseClosedEvent{}}

Here's an example of using the consumer:

    [dackerman@stacktrace clojure-kafka]$ lein run consume events
    ------------------------------------
    
    {:open (), :closed ("2b67ca4b-958c-417c-bde1-d8708536bc8b")}
     nil
    
    ------------------------------------
    
    {:open ("2b67ca4b-958c-417c-bde1-d8708536bc8b"),
     :closed ("2b67ca4b-958c-417c-bde1-d8708536bc8b")}
     nil


## License

Copyright © 2014 David Ackerman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
