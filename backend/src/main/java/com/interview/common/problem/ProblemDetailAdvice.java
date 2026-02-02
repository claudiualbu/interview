package com.interview.common.problem;

import com.interview.common.correlation.CorrelationId;
import com.interview.common.exception.ConflictException;
import com.interview.common.exception.NotFoundException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ProblemDetailAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(400);
        pd.setType(ProblemTypes.VALIDATION);
        pd.setTitle("Validation failed");
        pd.setDetail("Request validation failed");

        pd.setProperty("correlationId", currentCorrelationId());

        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toValidationError)
                .toList();

        pd.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(404);
        pd.setType(ProblemTypes.NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setDetail(ex.getMessage());
        pd.setProperty("correlationId", currentCorrelationId());

        return ResponseEntity.status(404).body(pd);
    }

    @ExceptionHandler({ConflictException.class,
            DataIntegrityViolationException.class,
            ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ProblemDetail> handleConflict(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(409);
        pd.setType(ProblemTypes.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        pd.setProperty("correlationId", currentCorrelationId());

        return ResponseEntity.status(409).body(pd);
    }

    private ValidationError toValidationError(FieldError fieldError) {
        return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private String currentCorrelationId() {
        String id = MDC.get(CorrelationId.MDC_KEY);
        return (id == null || id.isBlank()) ? "unknown" : id;
    }
}
