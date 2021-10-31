# Springwolf App Kafka Example
This example contains Async Api docs describing two microservices:
1. A producer for a topic `example-topic`
2. A consumer of a topic `example-topic`

A docker compose file is provided, which can be used to conveniently run the following services:
- Springwolf App
- Kafka broker and zookeeper
- Kafdrop, a Kafka UI provided to see messages published by Springwolf App

The services described by the Async Api docs are not included.

## Requirements
- Docker

## Usage
1. Make sure ports `8999` and `9000` are available. If not, the values may be changed in `docker-compose.yaml`.
2. From within `example/kafka`, run `$ docker compose up` and wait for services to start.
3. Visit `localhost:8999/asyncapi-ui.html` to view Springwolf App UI.
4. To publish a message, open a channel and click `Publish`. The message may be edited before publishing,
but is required to be a valid json.
5. To view the published messages:
   1. Visit `localhost:9000`.
   2. Click the topic name at the bottom of the screen.
   3. Click the `View Messages` button.
6. Press `Ctrl+C` to stop the docker containers.