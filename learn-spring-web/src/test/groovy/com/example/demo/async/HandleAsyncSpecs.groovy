package com.example.demo.async

import com.example.demo.async.asyncwrapper.OutboundAsyncWrapperService
import com.example.demo.async.service.HandleAsyncTestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException

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

    def """Test handling CompletableFuture objects by custom allOf API - should throw CompletionException with cause
            from the first exceptionally completed future (ordered by input of the allOf method)"""() {
        when:
        wrapperService.allOf(
                () -> handleAsyncTestService.failAfter2Second(),
                () -> handleAsyncTestService.succeed(null),
                () -> handleAsyncTestService.failAfter1Second()
        ).join()

        then:
        def e = thrown(CompletionException)
        e.getCause() instanceof IllegalStateException
        e.getCause().getMessage() == "From failAfter2Second()"
    }

    def "Test handling CompletableFuture objects by custom allOf API"() {
        when:
        def asyncResult = wrapperService.allOf(
                () -> handleAsyncTestService.succeed("1"),
                () -> handleAsyncTestService.succeed("2"),
                () -> handleAsyncTestService.succeed("3")
        ).join()

        then:
        asyncResult.get(0) as String == "Result of succeed(1)"
        asyncResult.get(1) as String == "Result of succeed(2)"
        asyncResult.get(2) as String == "Result of succeed(3)"
    }
}
