package com.example.demo.async.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspectImpl {

    @Around("@annotation(com.example.demo.async.utils.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch watch = new StopWatch();
        Class<?> classType = proceedingJoinPoint.getSignature().getDeclaringType();
        String methodName = proceedingJoinPoint.getSignature().getName();
        Logger log = LoggerFactory.getLogger(classType);

        try {
            watch.start();
            log.info("Start execute method {}", methodName);
            return proceedingJoinPoint.proceed();
        } finally {
            watch.stop();
            log.info("finished execute method {} in {} ms", methodName, watch.getTotalTimeMillis());
        }
    }
}
