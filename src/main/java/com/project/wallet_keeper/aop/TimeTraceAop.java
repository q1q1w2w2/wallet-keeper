package com.project.wallet_keeper.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Slf4j
public class TimeTraceAop {

    @Around("execution(* com.project.wallet_keeper.controller..*(..))")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.info("{}.{}: {}ms", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), totalTimeMillis);
        }
    }
}
