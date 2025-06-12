package com.edwin.rrhh_api.common.exception;

import com.edwin.rrhh_api.common.response.InvalidFieldResponse;
import com.edwin.rrhh_api.common.response.InvalidRequestBodyResponse;
import com.edwin.rrhh_api.common.response.SimpleMessageResponse;
import com.edwin.rrhh_api.modules.user.exception.SetUserActiveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<InvalidFieldResponse> invalidFields = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            FieldError fieldError = (FieldError) error;
            invalidFields.add(new InvalidFieldResponse(
                    fieldError.getCode(),
                    fieldError.getDefaultMessage(),
                    fieldError.getField(),
                    fieldError.getRejectedValue()
            ));
        });

        InvalidRequestBodyResponse invalidRequestBody = new InvalidRequestBodyResponse(
                "Invalid request body, check errors",
                invalidFields
        );

        return new ResponseEntity<>(invalidRequestBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleMessageResponse handleResourceNotFoundException(ResourceNotFoundException e) {
        return new SimpleMessageResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public InvalidFieldResponse handleInvalidDataException(InvalidDataException e) {
        return new InvalidFieldResponse(
                e.getType(),
                e.getMessage(),
                e.getField(),
                e.getValue()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public SimpleMessageResponse handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.error("{} in {}", e.getMessage(), e.getSource());
        return new SimpleMessageResponse(e.getMessage());
    }

    @ExceptionHandler(SetUserActiveException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public SimpleMessageResponse handleSetUserActiveException(SetUserActiveException e) {
        return new SimpleMessageResponse(e.getMessage());
    }
}
