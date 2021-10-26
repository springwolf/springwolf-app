# Springwolf App Amqp Example
This example contains Async Api docs describing two microservices:
1. A producer for a queue `example-queue`
2. A consumer of a queue `example-queue`

A docker compose file is provided, which can be used to conveniently run the following services:
- Springwolf App
- Amqp server and management UI

The services described by the Async Api docs are not included.

## Requirements
- Docker

## Usage
1. Make sure ports `8999`, `5672` and `15672` are available. If not, the values may be changed in `docker-compose.yaml`.
2. From within `example/amqp`, run `$ docker compose up` and wait for services to start.
3. Visit `localhost:8999/asyncapi-ui.html` to view Springwolf App UI.
4. To publish a message, open a channel and click `Publish`. The message may be edited before publishing,
but is required to be a valid json.
5. To view the published messages:
   1. Visit `localhost:15672` (username/password is guest/guest).
   2. Click the `Queues` tab and choose `example-queue`.
   3. Click the `Get messages` in the middle of the screen, and then the `Get Messages` button.
6. Press `Ctrl+C` to stop the docker containers.