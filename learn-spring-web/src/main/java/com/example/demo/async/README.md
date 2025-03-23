# Async demo

This demo is created and run with Java 17.

## How to test

1. Start `AsyncDemoApplication` with active profile `async` and following VM options (this will enable you to monitor
   application process using VisualVM).
   ```
   -Dcom.sun.management.jmxremote
   -Dcom.sun.management.jmxremote.port=9010
   -Dcom.sun.management.jmxremote.local.only=false
   -Dcom.sun.management.jmxremote.authenticate=false
   -Dcom.sun.management.jmxremote.ssl=false
   ```
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
5. If there is too many timeout error in test result, try increase number of core pool size in `AsyncConfig`.
   **(By default, artillery sets the timeout for HTTP request to 10 seconds)**
6. You can try switch `com.example.demo.async.concurrency-mode` property in `application-async.properties`
   between `ASYNC` and `REACTIVE` to try running the app in different asynchronous.
   NOTE:
   1. Both are using the same TaskExecutor bean for thread creation.
   2. The app is not fully reactive, reactive API is used only to replace @Async methods.

Note: The underlying APIs response are intentionally delayed in a way that a request to `POST /api/v1/product/apply`
will be answered after 3 seconds.
