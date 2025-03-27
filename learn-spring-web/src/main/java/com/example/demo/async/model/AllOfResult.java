package com.example.demo.async.model;

import java.util.List;
import lombok.Getter;

@Getter
public class AllOfResult {

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
