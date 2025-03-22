# Async demo
## How to test
1. Start `AsyncDemoApplication` with active profile `async` and following VM options (this will enable you to monitor application process using VisualVM).
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

Note: The underlying APIs response are intentionally delayed in a way that a request should be answered after 3 seconds.
