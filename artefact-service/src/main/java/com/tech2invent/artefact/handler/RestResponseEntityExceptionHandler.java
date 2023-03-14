package com.tech2invent.artefact.handler;

import com.tech2invent.artefact.error.*;
import com.tech2invent.artefact.exception.ArtefactNotFoundException;
import com.tech2invent.artefact.exception.ArtefactSchemaValidationErrorsException;
import com.tech2invent.artefact.exception.CollectionItemsSchemaValidationException;
import com.tech2invent.artefact.exception.CollectionMetaSchemaValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice()
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }



    /**
     * Handles ArtefactSchemaValidationErrorsException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(ArtefactSchemaValidationErrorsException.class)
    protected ResponseEntity<Object> artefactSchemaValidationErrors(ArtefactSchemaValidationErrorsException ex) {
        final var methodName = "::artefactSchemaValidationErrors ";
        log.debug("{} handling exception ArtefactSchemaValidationErrorsException with: {}",methodName, ex.toString());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        apiError.setSubErrors(ex.getValidationMessages().stream().map(EntitySchemaValidationError::new).collect(Collectors.toList()));
        return buildResponseEntity(apiError);
    }

    /**
     * Handles ArtefactSchemaValidationErrorsException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(CollectionMetaSchemaValidationException.class)
    protected ResponseEntity<Object> collectionSchemaValidationErrors(CollectionMetaSchemaValidationException ex) {
        log.debug("::artefactSchemaValidationErrors handling exception ArtefactSchemaValidationErrorsException with: {}",ex.toString());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());

        if(!ex.getSchemaValidationErrors().isEmpty())
            apiError.setSubErrors(ex.getSchemaValidationErrors().stream().map(EntitySchemaValidationError::new).collect(Collectors.toList()));
        else if(ex.getArtefactErrorsMap().size()>0) {
            List<ApiSubError> artefactErrors = new ArrayList<>();
            ex.getArtefactErrorsMap().forEach(
                    (artefactId, artefactErrorMessages) ->
                artefactErrors.add(new ArtefactSchemaValidationErrors(artefactId, artefactErrorMessages))
            );
            apiError.setSubErrors(artefactErrors);
        }

        return buildResponseEntity(apiError);
    }

    /**
     * Handles ArtefactSchemaValidationErrorsException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(CollectionItemsSchemaValidationException.class)
    protected ResponseEntity<Object> collectionItemsSchemaValidationErrors(CollectionItemsSchemaValidationException ex) {
        log.debug("::artefactSchemaValidationErrors handling exception ArtefactSchemaValidationErrorsException with: {}",ex.toString());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());

        if(ex.getArtefactErrorsMap().size()>0) {
            List<ApiSubError> artefactErrors = new ArrayList<>();
            ex.getArtefactErrorsMap().forEach(
                    (artefactId, artefactErrorMessages) ->
                        artefactErrors.add(new ArtefactSchemaValidationErrors(artefactId, artefactErrorMessages))
            );
            apiError.setSubErrors(artefactErrors);
        }
        return buildResponseEntity(apiError);
    }


    /**
     * Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(ArtefactNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(ArtefactNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }


    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.error("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }



    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
