package com.edwin.rrhh_api.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thrown when invalid request data (request body or URL parameters) is provided.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvalidDataException extends IllegalArgumentException {

    /**
     * Error code that identifies the type of error.
     */
    private String type;

    /**
     * Name of the field that caused the error.
     */
    private String field;

    /**
     * Error message.
     */
    private String message;

    /**
     * Description of the error, can be null.
     */
    private String description;

    /**
     * Current value of the field.
     */
    private Object value;

    public InvalidDataException(String type, String field, String message) {
        this.type = type;
        this.field = field;
        this.message = message;
    }

    public InvalidDataException(String type, String field, String message, Object value) {
        this.type = type;
        this.field = field;
        this.message = message;
        this.value = value;
    }
}
