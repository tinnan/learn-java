package com.example.demo.async

import com.example.demo.async.asyncwrapper.OutboundAsyncWrapperService
import com.example.demo.async.service.HandleAsyncTestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

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
        def succeedFuture = wrapperService.wrap(() -> handleAsyncTestService.succeed())
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
        succeedFuture.join() == "Result of succeed()"

        try {
            failAfter2SecondFuture.join()
        } catch (CompletionException cpe) {
            verifyAll(cpe.getCause()) {
                it.getClass() == IllegalStateException
                it.getMessage() == "From failAfter2Second()"
            }
        }

        try {
            failAfter1SecondFuture.join()
        } catch (CompletionException cpe) {
            verifyAll(cpe.getCause()) {
                it.getClass() == IllegalArgumentException
                it.getMessage() == "From failAfter1Second()"
            }
        }
    }
}
