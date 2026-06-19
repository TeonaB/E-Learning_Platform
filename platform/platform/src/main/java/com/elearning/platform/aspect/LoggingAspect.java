package com.elearning.platform.aspect;

import com.elearning.platform.exception.BadRequestException;
import com.elearning.platform.exception.ResourceNotFoundException;
import com.elearning.platform.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Definește pointcut-ul care vizează toate metodele din clasele aflate în pachetul de servicii
     */
    @Pointcut("within(com.elearning.platform.service.impl..*)")
    public void serviceMethodsPointcut() {}

    /**
     * Interceptează apelurile, loghează argumentele la intrare, valoarea returnată la ieșire și timpul de execuție.
     * Dacă se aruncă o excepție gravă, este logată ca ERROR, iar cele de business ca WARN.
     */
    @Around("serviceMethodsPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Logare intrare la nivel DEBUG
        log.debug("AOP Entry: {}.{}() with arguments = {}", className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            
            // Logare ieșire cu succes la nivel DEBUG
            log.debug("AOP Exit: {}.{}() executed in {} ms. Returned = {}", className, methodName, elapsedTime, result);
            return result;
        } catch (ResourceNotFoundException | BadRequestException | UnauthorizedException e) {
            long elapsedTime = System.currentTimeMillis() - start;
            // Logare excepție de business la nivel ERROR (fără stack trace)
            log.error("AOP Business Exception in {}.{}() after {} ms. Message: {}", className, methodName, elapsedTime, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            long elapsedTime = System.currentTimeMillis() - start;
            // Logare argumente invalide la nivel ERROR (fără stack trace)
            log.error("AOP Illegal argument in {}.{}() after {} ms. Arguments = {}, Message: {}", className, methodName, elapsedTime, Arrays.toString(args), e.getMessage());
            throw e;
        } catch (Throwable e) {
            long elapsedTime = System.currentTimeMillis() - start;
            // Logare excepție gravă la nivel ERROR (cu stack trace sau doar mesaj, conform log.error)
            log.error("AOP Exception in {}.{}() after {} ms. Message: {}", className, methodName, elapsedTime, e.getMessage(), e);
            throw e;
        }
    }
}
