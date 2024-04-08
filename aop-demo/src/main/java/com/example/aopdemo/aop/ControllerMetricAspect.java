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

    @Pointcut("within(com.example.aopdemo.controller..*)")
    public void packagePointcut() {
        // Targets all methods in all classes within package com.example.aopdemo.controller and subpackage.
    }
    @Pointcut("@target(com.example.aopdemo.aop.annotation.EnableElapsedTimeLog)")
    public void annotatedTypePointcut() {
    }

    /*
        !!! IMPORTANT !!!
        We should always limit pointcut scope. Pointcut annotatedTypePointcut() is considered a global scope, as
        every bean will be advised and you may get an error when trying to start the application due to exception
        starting filters.
     */
    @Around("packagePointcut() && annotatedTypePointcut()")
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
