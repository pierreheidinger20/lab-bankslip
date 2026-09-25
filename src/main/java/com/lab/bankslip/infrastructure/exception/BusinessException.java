package com.lab.bankslip.infrastructure.exception;


public class BusinessException  extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
