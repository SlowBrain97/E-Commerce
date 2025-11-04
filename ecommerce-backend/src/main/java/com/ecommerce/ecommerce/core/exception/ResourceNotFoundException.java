package com.ecommerce.ecommerce.core.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
              String.format("%s not found with id: %s", resourceName, resourceId));
    }

    public ResourceNotFoundException(ErrorCode errorCode, String resourceName, Object resourceId) {
        super(errorCode, String.format("%s not found with id: %s", resourceName, resourceId));
    }

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
