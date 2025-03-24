package com.example.demo.async

import com.example.demo.async.asyncwrapper.OutboundAsyncWrapperService
import com.example.demo.async.exception.AsyncException
import com.example.demo.async.model.Tuple3
import com.example.demo.async.service.HandleAsyncTestService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.util.StopWatch
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException

@Slf4j
@TestPropertySource(properties = [
        "com.example.demo.async.concurrency-mode=ASYNC"
])
@ActiveProfiles("async")
@SpringBootTest(classes = AsyncDemoApplication)
class HandleAsyncSpecs extends Specification {

    @Autowired
    OutboundAsyncWrapperService wrapperService
    @Autowired
    HandleAsyncTestService handleAsyncTestService
    StopWatch stopWatch

    def setup() {
        stopWatch = new StopWatch()
        stopWatch.start()
    }

    def cleanup() {
        stopWatch.stop()
        log.info("Test elapsed time: {} ms", stopWatch.getTotalTimeMillis())
    }

    def "Test handling CompletableFuture objects"() {
        given:
        /*
        There is also CompletableFuture.supplyAsync method but it won't be able to support
        Spring bean inheritance from parent thread? Because it does not use Spring task executor bean to
        execute async method.
         */
        def failAfter2SecondFuture = wrapperService.wrap(() -> handleAsyncTestService.failAfter2Second())
        def succeedFuture = wrapperService.wrap(() -> handleAsyncTestService.succeed(null))
        def failAfter1SecondFuture = wrapperService.wrap(() -> handleAsyncTestService.failAfter1Second())

        when:
        CompletableFuture.allOf(failAfter2SecondFuture, succeedFuture, failAfter1SecondFuture).join()

        then: "allOf.join() should throw CompletionException with exception from first exceptionally completed future as cause"
        def e = thrown(CompletionException)
        verifyAll(e.getCause()) {
            /*
            Cause of exception here is sorted by order of argument to allOf method.
            From example in this case, exception from failAfter2Second() is return in this case
            even though it takes longest to complete.
            */
            it.getClass() == IllegalStateException
            it.getMessage() == "From failAfter2Second()"
        }

        and: "check result of each future individually"
        succeedFuture.get() == "Result of succeed(null)"

        when:
        failAfter2SecondFuture.get()

        then:
        def e1 = thrown(ExecutionException)
        e1.getCause() instanceof IllegalStateException
        e1.getCause().getMessage() == "From failAfter2Second()"

        when:
        failAfter1SecondFuture.get()

        then:
        def e2 = thrown(ExecutionException)
        e2.getCause() instanceof IllegalArgumentException
        e2.getCause().getMessage() == "From failAfter1Second()"
    }

    def """Test handling CompletableFuture objects by custom allOf API - should throw AsyncException with cause
            from the first exceptionally completed future (ordered by input of the allOf method)"""() {
        when:
        OutboundAsyncWrapperService.allOf(
                wrapperService.wrap(() -> handleAsyncTestService.failAfter2Second()),
                wrapperService.wrap(() -> handleAsyncTestService.succeed("1")),
                wrapperService.wrap(() -> handleAsyncTestService.failAfter1Second()),
        )

        then:
        def e = thrown(AsyncException)
        e.getCause() instanceof IllegalStateException
        e.getCause().getMessage() == "From failAfter2Second()"

        def results = e.getResults()
        verifyAll(results[0]) {
            it.isError()
            it.getData() == null
            it.getError() instanceof IllegalStateException
            it.getError().getMessage() == "From failAfter2Second()"
        }
        verifyAll(results[1]) {
            !it.isError()
            it.getData() == "Result of succeed(1)"
        }
        verifyAll(results[2]) {
            it.isError()
            it.getData() == null
            it.getError() instanceof IllegalArgumentException
            it.getError().getMessage() == "From failAfter1Second()"
        }
    }

    def "Test handling CompletableFuture objects by custom allOf API"() {
        when:
        Tuple3<String, Void, HandleAsyncTestService.SuccessResponse> asyncResult = OutboundAsyncWrapperService.allOf(
                wrapperService.wrap(() -> handleAsyncTestService.succeed("1")),
                wrapperService.wrap(() -> handleAsyncTestService.succeedVoid()),
                wrapperService.wrap(() -> handleAsyncTestService.succeed()),
        )
        String r1 = asyncResult.getT1()
        Void r2 = asyncResult.getT2()
        HandleAsyncTestService.SuccessResponse r3 = asyncResult.getT3()

        then:
        r1 == "Result of succeed(1)"
        r2 == null
        r3.name == "John"
    }
}
