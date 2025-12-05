package com.estim.javaapi.presentation.common;

public record ErrorResponse(
    String code,
    String message,
    String details
) {}
