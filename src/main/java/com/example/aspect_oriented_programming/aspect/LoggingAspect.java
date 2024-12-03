package com.example.aspect_oriented_programming.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.example.aspect_oriented_programming.service.TaskService.*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Before advice: начало выполнения метода {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("AfterThrowing advice: исключение в методе {}: {}", joinPoint.getSignature().getName(), exception.getMessage());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturningResult(JoinPoint joinPoint, Object result) {
        logger.info("AfterReturning advice: метод {} вернул результат: {}", joinPoint.getSignature().getName(), result);
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        logger.info("Around advice: метод {} начинается...", joinPoint.getSignature().getName());
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable exception) {
            logger.error("Метод {} выбросил исключение: {}", joinPoint.getSignature().getName(), exception.getMessage());
            throw new RuntimeException(exception);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info("Метод {} завершился за {} мс.", joinPoint.getSignature().getName(), elapsedTime);
        return result;
    }
}
