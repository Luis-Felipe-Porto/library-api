package com.porto.libraryapi.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensage) {
        super(mensage);
    }
}
