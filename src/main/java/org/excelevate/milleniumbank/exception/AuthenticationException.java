package org.excelevate.milleniumbank.exception;

public class AuthenticationException extends Exception {
    String errorId;

    public AuthenticationException(String message, Throwable cause, String errorId) {
        super(message, cause);
        this.errorId = errorId;
    }


}
