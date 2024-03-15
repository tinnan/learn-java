## Usage

1. Run docker compose to start Kafka.
2. Start the app.
3. Send message via HTTP request.
```shell
curl --location --request POST 'http://localhost:8080/api/v1/messages' \
--header 'Content-Type: application/json' \
--data-raw '{
    "message": "<Your message>"
}'
```