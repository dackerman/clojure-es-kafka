# clojure-kafka

FIXME: description

## Installation

Download from https://github.com/dackerman/clojure-es-kafka.

## Usage

FIXME: explanation

    $ java -jar clojure-kafka-0.1.0-standalone.jar [args]

## Options

produce: start a producer that takes commands as input and creates events based on a clojure domain model. It applies
         them to the in-memory event store and publishes those events to a kafka stream.
consume: start a kafka consumer and transform the events to a projection as data comes in.

## Examples


## License

Copyright © 2014 David Ackerman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
