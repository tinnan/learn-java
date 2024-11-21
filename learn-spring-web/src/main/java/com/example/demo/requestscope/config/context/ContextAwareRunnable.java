package com.example.demo.requestscope.config.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextAwareRunnable implements Runnable {

    private final CustomRequestScopeAttr requestAttributes;
    private final Runnable task;

    public ContextAwareRunnable(RequestAttributes requestAttributes, Runnable task) {
        this.task = task;
        this.requestAttributes = ContextUtils.cloneRequestAttributes(requestAttributes);
    }

    @Override
    public void run() {
        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            task.run();
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
