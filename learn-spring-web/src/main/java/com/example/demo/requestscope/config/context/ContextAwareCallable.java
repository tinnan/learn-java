package com.example.demo.requestscope.config.context;

import java.util.concurrent.Callable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextAwareCallable<T> implements Callable<T> {

    private final CustomRequestScopeAttr requestAttributes;
    private final Callable<T> task;

    public ContextAwareCallable(RequestAttributes requestAttributes, Callable<T> task) {
        this.requestAttributes = ContextUtils.cloneRequestAttributes(requestAttributes);
        this.task = task;
    }

    @Override
    public T call() throws Exception {
        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            return task.call();
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
