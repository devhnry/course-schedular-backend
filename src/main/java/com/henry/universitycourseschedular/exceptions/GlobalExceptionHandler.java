package com.henry.universitycourseschedular.exceptions;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage());
        DefaultApiResponse<?> response =
                buildErrorResponse(String.format("Unexpected Error Occurred: (%s)", ex.getMessage()), StatusCodes.GENERIC_FAILURE);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleInvalidArgument(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> errors.put( error.getField() , error.getDefaultMessage()));
        log.error("Validation Failed: ({})", e.getMessage());

        DefaultApiResponse<?> response =
                buildErrorResponse("Validation Failed", StatusCodes.GENERIC_FAILURE, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleJsonParseException(HttpMessageConversionException ex)
    {
        log.error("JsonParseException: {}", ex.getMessage());
        DefaultApiResponse<?> response =
                buildErrorResponse(ex.getMessage(), StatusCodes.GENERIC_FAILURE);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleExpiredJWTException(ExpiredJwtException ex)
    {
        log.warn("Expired JWT Exception: {}", ex.getMessage());
        DefaultApiResponse<?> response =
                buildErrorResponse("JWT Expired: Prompt user to Login or Refresh Token", StatusCodes.JWT_EXPIRED);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleJwtSignatureExceptions(SignatureException ex)
    {
        log.error("Signature Exception {}", ex.getMessage());
        DefaultApiResponse<?> response =
                buildErrorResponse("JWT Expired: Prompt user to Login or Refresh Token", StatusCodes.JWT_SIGNATURE_EXPIRED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

//    public ResponseEntity<DefaultApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex)
//    {
//        log.error("Resource Not Found Exception: {}", ex.getMessage());
//        DefaultApiResponse<?> response = new DefaultApiResponse<>();
//        response.setStatusCode(StatusCodes.RESOURCE_NOT_FOUND);
//        response.setStatusMessage(ex.getMessage());
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }
}
