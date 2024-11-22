package com.example.demo.requestscope.config.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
public class ContextAwareRunnable implements Runnable {

    private final CustomRequestScopeAttr requestAttributes;
    private final Runnable task;

    public ContextAwareRunnable(Runnable task) {
        this.requestAttributes = ContextUtils.cloneRequestAttributes();
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
