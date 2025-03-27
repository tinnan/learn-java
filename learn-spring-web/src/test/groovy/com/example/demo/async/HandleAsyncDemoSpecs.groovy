package com.example.demo.async

import com.example.demo.async.service.HandleAsyncDemoService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.util.StopWatch
import spock.lang.Specification

@Slf4j
@TestPropertySource(properties = [
        "com.example.demo.async.concurrency-mode=ASYNC"
])
@ActiveProfiles("async")
@SpringBootTest(classes = AsyncDemoApplication)
class HandleAsyncDemoSpecs extends Specification {

    @Autowired
    HandleAsyncDemoService handleAsyncDemoService
    StopWatch stopWatch

    def setup() {
        stopWatch = new StopWatch()
        stopWatch.start()
    }

    def cleanup() {
        stopWatch.stop()
        log.info("Test elapsed time: {} ms", stopWatch.getTotalTimeMillis())
    }

    def "Should return response given no error occurs in async process"() {
        when:
        def response = handleAsyncDemoService.runFlow("cx1", 9)

        then:
        verifyAll {
            response.api1.transactionId == "api-1-txn-cx1"
            response.api3.transactionId == "api-3-txn"
            response.api4.transactionId == "api-4-txn"
        }
    }

    def "Should throw exception from inside async process given an error occurs"() {
        when:
        handleAsyncDemoService.runFlow("cx1", 10)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "API 4 - Invalid customer status"
    }
}
