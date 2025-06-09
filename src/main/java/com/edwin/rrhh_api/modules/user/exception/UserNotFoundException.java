package com.edwin.rrhh_api.modules.user.exception;

import com.edwin.rrhh_api.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
