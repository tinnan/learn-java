package com.github.tinnan.jobrunner.config;

import org.springframework.core.task.TaskDecorator;

public class JobTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        String configValue = JobConfigHolder.get();
        return () -> {
            try {
                JobConfigHolder.set(configValue);
                runnable.run();
            } finally {
                JobConfigHolder.clear();
            }
        };
    }
}
