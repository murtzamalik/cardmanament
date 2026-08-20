package com.cms.app.exception;

import com.cms.app.response.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, List<String>>>> handleValidation(MethodArgumentNotValidException ex) {
        ResponseWrapper<Map<String, List<String>>> wrapper = new ResponseWrapper<>();
        wrapper.setResponseCode(HttpStatus.BAD_REQUEST.value());
        wrapper.setResponseMessage(HttpStatus.BAD_REQUEST.name());
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            errors.add(field + ":" + error.getDefaultMessage());
        });
        Map<String, List<String>> body = new HashMap<>();
        body.put("errors", errors);
        wrapper.setResponseBody(body);
        return ResponseEntity.badRequest().body(wrapper);
    }
}
