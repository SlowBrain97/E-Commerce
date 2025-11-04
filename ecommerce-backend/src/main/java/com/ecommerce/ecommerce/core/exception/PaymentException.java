package com.ecommerce.ecommerce.core.exception;

/**
 * Exception thrown when payment processing fails
 */
public class PaymentException extends BaseException {

    public PaymentException(String message) {
        super(ErrorCode.PAYMENT_FAILED, message);
    }

    public PaymentException(String message, Throwable cause) {
        super(ErrorCode.PAYMENT_FAILED, message, cause);
    }

    public PaymentException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
