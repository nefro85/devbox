package io.s7i.webauthn;

public class CannotAddAlreadyExistsException extends RuntimeException {

    public CannotAddAlreadyExistsException(String message) {
        super(message);
    }
}
