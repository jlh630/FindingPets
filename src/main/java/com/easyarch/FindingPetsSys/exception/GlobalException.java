package com.easyarch.FindingPetsSys.exception;

import com.easyarch.FindingPetsSys.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalException {


    @ExceptionHandler({ConstraintViolationException.class, ValidatorException.class})
    public Result<String> handleValidException(Exception e) {
        log.warn("ValidatorException :{}", e.getMessage());
        return Result.badRequest(e.getMessage());
    }

    @ExceptionHandler({AuthenticationException.class})
    public Result<String> handleAuthenticationException(AuthenticationException e) {
        log.warn("AuthenticationException :{}", e.getMessage());
        return Result.unAuthorized(e.getMessage());
    }

    @ExceptionHandler({NotFoundException.class})
    public Result<String> handleNotFoundException(NotFoundException e) {
        log.warn("NotFoundException :{}", e.getMessage());
        return Result.notFound(e.getMessage());
    }

    @ExceptionHandler({OperationFailedException.class})
    public Result<String> handleOperationFailedException(OperationFailedException e) {
        log.warn("OperationFailedException :{}", e.getMessage());
        return Result.conflict(e.getMessage());
    }

    @ExceptionHandler({ServiceException.class})
    public Result<String> handleServiceException(ServiceException e) {
        log.error(e.getMessage());
        return Result.error("服务器异常");
    }

    @ExceptionHandler({ServletRequestBindingException.class})
    public Result<String> handleArgsException(Exception e) {
        log.warn("ValidatorException :{}", e.getMessage());
        return Result.badRequest("参数异常");
    }

    @ExceptionHandler({RuntimeException.class})
    public Result<String> handleRunTimeException(RuntimeException e) {
        if (e.getCause() instanceof NumberFormatException) {
            log.warn("ValidatorException :{}", e.getMessage());
            return Result.badRequest("参数异常");
        }

        StackTraceElement[] stackTrace = e.getStackTrace();
        StackTraceElement firstElement = stackTrace.length > 0 ? stackTrace[0] : null;

        // 构建错误消息
        String errorMessage = "An error occurred: " + e.getMessage();
        if (firstElement != null) {
            errorMessage += " at " + firstElement;
        }
        log.error(errorMessage);
        return Result.error("操作失败");
    }


}