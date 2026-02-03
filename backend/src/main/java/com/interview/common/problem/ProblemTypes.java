package com.interview.common.problem;

import java.net.URI;

public final class ProblemTypes {

    private ProblemTypes() {

    }

    public static final URI VALIDATION = URI.create("https://example.com/problems/validation");
    public static final URI NOT_FOUND = URI.create("https://example.com/problems/not-found");
    public static final URI CONFLICT = URI.create("https://example.com/problems/conflict");
    public static final URI INTERNAL_ERROR = URI.create("https://example.com/problems/internal-error");
}
