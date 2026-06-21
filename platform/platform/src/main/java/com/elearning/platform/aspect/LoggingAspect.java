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
     * Prag (in milisecunde) peste care o metoda de serviciu este considerata lenta
     * si este logata la nivel WARN pentru analiza de performanta.
     */
    private static final long SLOW_EXECUTION_THRESHOLD_MS = 500;

    /**
     * Definește pointcut-ul care vizează toate metodele din clasele aflate în pachetul de servicii
     */
    @Pointcut("within(com.elearning.platform.service.impl..*)")
    public void serviceMethodsPointcut() {}

    /**
     * Interceptează apelurile, loghează argumentele și timpul de execuție.
     * Logarea pe DEBUG este protejată prin isDebugEnabled() pentru a evita construirea
     * inutilă a string-urilor pe hot path când nivelul nu este activ.
     * Excepțiile de business sunt logate ca WARN, iar cele grave ca ERROR.
     */
    @Around("serviceMethodsPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Construim reprezentarea argumentelor doar dacă DEBUG este activ
        if (log.isDebugEnabled()) {
            log.debug("AOP Entry: {}.{}() with arguments = {}",
                    className, methodName, Arrays.toString(joinPoint.getArgs()));
        }

        long start = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            // Semnalare executie lenta la nivel WARN
            if (elapsedMs > SLOW_EXECUTION_THRESHOLD_MS) {
                log.warn("AOP Slow execution: {}.{}() took {} ms (threshold {} ms)",
                        className, methodName, elapsedMs, SLOW_EXECUTION_THRESHOLD_MS);
            }

            if (log.isDebugEnabled()) {
                log.debug("AOP Exit: {}.{}() executed in {} ms. Returned = {}",
                        className, methodName, elapsedMs, result);
            }
            return result;
        } catch (ResourceNotFoundException | BadRequestException | UnauthorizedException e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            // Logare excepție de business la nivel WARN (fără stack trace)
            log.warn("AOP Business Exception in {}.{}() after {} ms. Message: {}",
                    className, methodName, elapsedMs, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            // Logare argumente invalide la nivel ERROR (fără stack trace)
            log.error("AOP Illegal argument in {}.{}() after {} ms. Arguments = {}, Message: {}",
                    className, methodName, elapsedMs, Arrays.toString(joinPoint.getArgs()), e.getMessage());
            throw e;
        } catch (Throwable e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            // Logare excepție gravă la nivel ERROR (cu stack trace)
            log.error("AOP Exception in {}.{}() after {} ms. Message: {}",
                    className, methodName, elapsedMs, e.getMessage(), e);
            throw e;
        }
    }
}
