package com.henry.universitycourseschedular.utils;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseUtil {

    public static <T> DefaultApiResponse<T> buildErrorResponse(String message) {
        return buildResponse(StatusCodes.GENERIC_FAILURE, message, null);
    }

    public static <T> DefaultApiResponse<T> buildErrorResponse(String message, int statusCode) {
        return buildResponse(statusCode, message, null);
    }

    public static <T> DefaultApiResponse<T> buildErrorResponse(String message, int statusCode, T data) {
        return buildResponse(statusCode, message, data);
    }

    public static <T> DefaultApiResponse<T> buildSuccessResponse(String message) {
        return buildResponse(StatusCodes.ACTION_COMPLETED, message, null);
    }

    public static <T> DefaultApiResponse<T> buildSuccessResponse(String message, int statusCode) {
        return buildResponse(statusCode, message, null);
    }

    public static <T> DefaultApiResponse<T> buildSuccessResponse(String message, int statusCode, T data) {
        return buildResponse(statusCode, message, data);
    }

    private static <T> DefaultApiResponse<T> buildResponse(int statusCode, String message, T data) {
        DefaultApiResponse<T> response = new DefaultApiResponse<>();
        response.setStatusCode(statusCode);
        response.setStatusMessage(message);
        response.setData(data);
        return response;
    }
}