package com.example.aopdemo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ControllerMetricAspect {

    @Pointcut("within(com.example.aopdemo.controller.*)")
    public void controllerTargetedPointcut() {
        // Targets all methods in all controllers within package com.example.aopdemo.controller
        // NOTE: It does target classes under sub-package. Alternatively: create a custom annotation to annotate the
        // target class and use @target in pointcut instead.
    }

    @Around("controllerTargetedPointcut()")
    public Object logExecutionElapsedTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("Method [{}.{}] execution elapsed: {} ms",
                    joinPoint.getTarget()
                            .getClass()
                            .getName(), joinPoint.getSignature()
                            .getName(), end - start);
        }
    }
}
