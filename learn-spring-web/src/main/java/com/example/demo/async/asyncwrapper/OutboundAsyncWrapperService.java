package com.example.demo.async.asyncwrapper;

import com.example.demo.async.exception.AsyncException;
import com.example.demo.async.model.AsyncResult;
import com.example.demo.async.model.Tuple2;
import com.example.demo.async.model.Tuple3;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OutboundAsyncWrapperService {

    @Async
    public <R> CompletableFuture<R> wrap(Supplier<R> supplier) {
        return CompletableFuture.completedFuture(supplier.get());
    }

    public static <T1, T2> Tuple2<T1, T2> allOf(
        CompletableFuture<? extends T1> future1,
        CompletableFuture<? extends T2> future2
    ) throws AsyncException {
        AllOfResult result = all(future1, future2);
        return new Tuple2<>(result.getData(0), result.getData(1));
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> allOf(
        CompletableFuture<? extends T1> completableFuture1,
        CompletableFuture<? extends T2> completableFuture2,
        CompletableFuture<? extends T3> completableFuture3
    ) throws AsyncException {
        AllOfResult result = all(completableFuture1, completableFuture2, completableFuture3);
        return new Tuple3<>(result.getData(0), result.getData(1),
            result.getData(2));
    }

    private static AllOfResult all(CompletableFuture<?>... completableFutures) throws AsyncException {
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
            throw new AsyncException(result.getResultList(), result.getFirstError());
        }
        return result;
    }

    @Getter
    static class AllOfResult {

        private final List<AsyncResult> resultList;
        private Exception firstError = null;

        public AllOfResult(List<AsyncResult> resultList) {
            if (resultList == null || resultList.isEmpty()) {
                throw new IllegalArgumentException("resultList must not be null or empty");
            }
            this.resultList = resultList;
            for (AsyncResult r : resultList) {
                if (r.isError()) {
                    this.firstError = r.getError();
                    break;
                }
            }
        }

        public <R> R getData(int index) {
            return this.resultList.get(index).getData();
        }

        public boolean isExceptionallyCompleted() {
            return this.firstError != null;
        }
    }
}
