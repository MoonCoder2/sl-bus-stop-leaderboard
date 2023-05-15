package com.example.sbabchallenge.controller;

import com.example.sbabchallenge.model.TrafiklabException;
import com.example.sbabchallenge.model.TrafiklabParseException;
import com.example.sbabchallenge.model.TrafiklabValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value= { TrafiklabValidationException.class, TrafiklabException.class, TrafiklabParseException.class})
    protected ResponseEntity<Object> handleTrafiklabException(
            RuntimeException ex, WebRequest request) {

        String msg = "";
        if(ex instanceof TrafiklabException){
            msg = "Trafiklab did not return 2XX status, status=" + ((TrafiklabException) ex).getStatusCode();
        }else if (ex instanceof TrafiklabParseException || ex instanceof TrafiklabValidationException){
            msg = "Could not parse response from Trafiklab";

        }
        logger.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(msg);
    }
}
