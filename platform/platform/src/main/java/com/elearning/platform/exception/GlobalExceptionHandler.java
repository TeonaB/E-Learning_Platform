package com.elearning.platform.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException caught: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("error", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(BadRequestException.class)
    public ModelAndView handleBadRequest(BadRequestException ex) {
        log.error("BadRequestException caught: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("error", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView handleUnauthorized(UnauthorizedException ex) {
        log.error("UnauthorizedException caught: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("error", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("Static resource not found: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("error", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException caught: ", ex);
        ModelAndView mav = new ModelAndView("error/400");
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        mav.addObject("error", "Eroare bază de date: " + message);
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidation(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException caught: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400");
        String message = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validare eșuată"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        mav.addObject("error", message);
        return mav;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolation(ConstraintViolationException ex) {
        log.error("ConstraintViolationException caught: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400");
        String message = ex.getConstraintViolations().isEmpty()
                ? "Validare eșuată"
                : ex.getConstraintViolations().iterator().next().getMessage();
        mav.addObject("error", message);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleOther(Exception ex) {
        log.error("Unhandled exception caught: ", ex);
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("error", ex.getMessage());
        return mav;
    }
}
