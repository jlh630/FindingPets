

package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private static final Integer OK = 200;
    private static final Integer CREATED = 201;
    private static final Integer NO_CONTENT = 204;
    private static final Integer BAD_REQUEST = 400;
    private static final Integer UNAUTHORIZED = 401;
    private static final Integer BAN = 403;
    private static final Integer NOT_FOUND = 404;
    private static final Integer CONFLICT = 409;
    private static final Integer ERROR = 500;
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(OK, "success!", data);
    }

    public static <T> Result<T> created(String message, T data) {
        return new Result<>(CREATED, message, data);
    }

    public static Result<String> operate(String message) {
        return new Result<>(NO_CONTENT, message, null);
    }

    public static <T> Result<T> badRequest(String message) {
        return new Result<>(BAD_REQUEST, message, null);
    }

    public static Result<String> ban(String message) {
        return new Result<>(BAN, message, null);
    }

    public static Result<String> unAuthorized(String message) {
        return new Result<>(UNAUTHORIZED, message, null);
    }

    public static Result<String> notFound(String message) {
        return new Result<>(NOT_FOUND, message, null);
    }

    public static Result<String> conflict(String message) {
        return new Result<>(CONFLICT, message, null);
    }

    public static Result<String> error(String message) {
        return new Result<>(ERROR, message, null);
    }

    
}
