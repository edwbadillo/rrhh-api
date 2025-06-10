package com.edwin.rrhh_api.common.response;

import java.util.List;

/**
 * Represents an invalid request body to be sent to the client.
 */
public record InvalidRequestBodyResponse(
        String message,
        List<InvalidFieldResponse> errors
) {
}
