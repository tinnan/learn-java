package com.example.demo.async.asyncwrapper;

import com.example.demo.async.exception.AsyncAllOfException;
import com.example.demo.async.model.AllOfResult;
import com.example.demo.async.model.AsyncResult;
import com.example.demo.async.model.Tuple2;
import com.example.demo.async.model.Tuple3;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OutboundAsyncWrapperService {

    @Async
    public <R> CompletableFuture<R> wrap(Supplier<R> supplier) {
        return CompletableFuture.completedFuture(supplier.get());
    }

    public static <T1, T2> Tuple2<T1, T2> all(
        CompletableFuture<?> future1,
        CompletableFuture<?> future2
    ) throws AsyncAllOfException {
        AllOfResult result = allOf(future1, future2);
        return new Tuple2<>(result.getData(0), result.getData(1));
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> all(
        CompletableFuture<?> completableFuture1,
        CompletableFuture<?> completableFuture2,
        CompletableFuture<?> completableFuture3
    ) throws AsyncAllOfException {
        AllOfResult result = allOf(completableFuture1, completableFuture2, completableFuture3);
        return new Tuple3<>(result.getData(0), result.getData(1),
            result.getData(2));
    }

    public static AllOfResult allOf(CompletableFuture<?>... completableFutures) throws AsyncAllOfException {
        CompletableFuture.allOf(completableFutures);
        List<AsyncResult> resultList = new ArrayList<>();
        for (CompletableFuture<?> completableFuture : completableFutures) {
            try {
                resultList.add(new AsyncResult(completableFuture.get(), null));
            } catch (ExecutionException e) {
                resultList.add(new AsyncResult(null, (Exception) e.getCause()));
            } catch (Exception e) {
                resultList.add(new AsyncResult(null, e));
            }
        }
        AllOfResult result = new AllOfResult(resultList);
        if (result.isExceptionallyCompleted()) {
            throw new AsyncAllOfException(result.getResultList(), result.getFirstError());
        }
        return result;
    }
}
