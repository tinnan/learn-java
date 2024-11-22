package com.example.demo.requestscope.config.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
public class ContextAwareRunnable implements Runnable {

    private final CustomRequestScopeAttr requestAttributes;
    private final Runnable task;

    public ContextAwareRunnable(RequestAttributes requestAttributes, Runnable task) {
        this.requestAttributes = ContextUtils.cloneRequestAttributes(requestAttributes);
        this.task = task;
    }

    @Override
    public void run() {
        try {
            log.info("ContextAwareRunnable - run");
            RequestContextHolder.setRequestAttributes(requestAttributes);
            task.run();
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
