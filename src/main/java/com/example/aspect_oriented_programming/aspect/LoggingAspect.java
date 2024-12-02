package com.example.aspect_oriented_programming.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Pointcut("execution(* com.example.aspect_oriented_programming.service.TaskService.*(..))")
    public void serviceMethods() {
    }

    @Before("serviceMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Before advice: Начало выполнения метода " + joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.severe("AfterThrowing advice: Исключение в методе " + joinPoint.getSignature().getName()
                + " с сообщением: " + exception.getMessage());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturningResult(JoinPoint joinPoint, Object result) {
        logger.info("AfterReturning advice: Метод " + joinPoint.getSignature().getName()
                + " вернул результат: " + result);
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        logger.info("Around advice: Метод " + joinPoint.getSignature().getName() + " начинается...");
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable exception) {
            logger.severe("Around advice:  Метод " + joinPoint.getSignature().getName()
                    + " выбросил исключение: " + exception.getMessage());

            return Optional.empty();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info("Around advice: Метод " + joinPoint.getSignature().getName()
                + " завершился за " + elapsedTime + " мс.");
        return result;
    }

}
