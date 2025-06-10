package com.edwin.rrhh_api.common.response;

/**
 * Represents an invalid data response to be sent to the client.
 */
public record InvalidFieldResponse(
        String type,
        String message,
        String field,
        Object value
) {
}
