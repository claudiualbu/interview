package com.interview.common.problem;

public record ValidationError(
        String field,
        String message
) {
}
