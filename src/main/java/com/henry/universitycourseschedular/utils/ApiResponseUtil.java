package com.henry.universitycourseschedular.utils;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseUtil {

    public static <T> DefaultApiResponse<T> buildErrorResponse(String message){
        DefaultApiResponse<T> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.GENERIC_FAILURE);
        response.setStatusMessage(message);
        return response;
    }

    public static <T> DefaultApiResponse<T> buildErrorResponse(String message, int statusCode){
        DefaultApiResponse<T> response = new DefaultApiResponse<>();
        response.setStatusCode(statusCode);
        response.setStatusMessage(message);
        return response;
    }

    public static <T> DefaultApiResponse<T> buildErrorResponse(String message, int statusCode, T data){
        DefaultApiResponse<T> response = new DefaultApiResponse<>();
        response.setStatusCode(statusCode);
        response.setStatusMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> DefaultApiResponse<T> buildSuccessResponse(String message){
        DefaultApiResponse<T> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.ACTION_COMPLETED);
        response.setStatusMessage(message);
        return response;
    }

    public static <T> DefaultApiResponse<T> buildSuccessResponse(String message, int statusCode){
        DefaultApiResponse<T> response = new DefaultApiResponse<>();
        response.setStatusCode(statusCode);
        response.setStatusMessage(message);
        return response;
    }

    public static <T> DefaultApiResponse<T> buildSuccessResponse(String message, int statusCode, T data){
        DefaultApiResponse<T> response = new DefaultApiResponse<>();
        response.setStatusCode(statusCode);
        response.setStatusMessage(message);
        response.setData(data);
        return response;
    }
}
