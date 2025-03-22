# Async demo
## How to test
1. Start `AsyncDemoApplication` with active profile `async`.
2. Test call API
    ```shell
    curl --location 'http://localhost:8080/api/v1/product/apply' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "customerEmail": "test@test.com",
        "productId": "1"
    }'
    ```
3. Install load testing tool `npm install -g artillery`.
4. Run command
   ```shell
   artillery run ./src/main/resources/loadtest/async-loadtest-spec.yaml
   ```

Note: The underlying APIs response are intentionally delayed in a way that a request should be answered after 3 seconds.
