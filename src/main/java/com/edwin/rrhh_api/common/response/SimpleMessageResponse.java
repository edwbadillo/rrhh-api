package com.edwin.rrhh_api.common.response;

/**
 * Represents a simple message response to be sent to the client.
 *
 * @param message the message to display
 */
public record SimpleMessageResponse(
        String message
) {
}
