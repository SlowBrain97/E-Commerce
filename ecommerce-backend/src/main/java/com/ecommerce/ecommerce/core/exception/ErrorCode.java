package com.ecommerce.ecommerce.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // General errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "Internal server error"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "E002", "Input validation failed"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "E003", "Access denied"),

    // Resource errors
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E100", "Resource not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "E101", "Product not found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "E102", "Category not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E103", "User not found"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "E104", "Review not found"),

    // Business logic errors
    PRODUCT_CREATION_FAILED(HttpStatus.BAD_REQUEST, "E200", "Failed to create product"),
    PRODUCT_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "E201", "Failed to update product"),
    PRODUCT_DELETE_FAILED(HttpStatus.BAD_REQUEST, "E202", "Failed to delete product"),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "E203", "Product is out of stock"),
    PRODUCT_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "E204", "Product is not active"),
    CATEGORY_CREATION_FAILED(HttpStatus.BAD_REQUEST, "E205", "Failed to create category"),
    CATEGORY_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "E206", "Failed to update category"),
    CATEGORY_DELETE_FAILED(HttpStatus.BAD_REQUEST, "E207", "Failed to delete category"),
    CATEGORY_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "E208", "Category is not active"),
    CATEGORY_NAME_EXISTS(HttpStatus.CONFLICT, "E209", "Category name already exists"),
    REVIEW_CREATION_FAILED(HttpStatus.BAD_REQUEST, "E210", "Failed to create review"),
    REVIEW_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "E211", "Failed to update review"),
    REVIEW_DELETE_FAILED(HttpStatus.BAD_REQUEST, "E212", "Failed to delete review"),
    ORDER_CREATION_FAILED(HttpStatus.BAD_REQUEST, "E213", "Failed to create order"),
    ORDER_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "E214", "Failed to update order"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "E215", "Order not found"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "E216", "Cart item not found"),
    PAYMENT_FAILED(HttpStatus.PAYMENT_REQUIRED, "E300", "Payment failed"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "E301", "Insufficient stock available"),
    DUPLICATE_ENTRY(HttpStatus.CONFLICT, "E302", "Duplicate entry found"),

    // Authentication/Authorization errors
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E400", "Unauthorized access"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E401", "Forbidden access"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "E402", "Invalid credentials"),

    // Legacy
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "E500", "Username not found"), AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "E217","Failed to authenticate" ),
    USERNAME_OR_EMAIL_EXISTED(HttpStatus.BAD_REQUEST,"E505","Username or email existed");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
