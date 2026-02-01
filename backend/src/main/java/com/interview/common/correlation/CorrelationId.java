package com.interview.common.correlation;

public final class CorrelationId {

    private CorrelationId() {

    }

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";
}
