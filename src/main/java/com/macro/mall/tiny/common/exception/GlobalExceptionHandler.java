package com.macro.mall.tiny.common.exception;

import com.macro.mall.tiny.common.api.CommonResult;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理主动抛出的业务异常。
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<CommonResult<Void>> handleApiException(
            ApiException exception
    ) {
        CommonResult<Void> body = CommonResult.failed(
                exception.getErrorCode(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * 处理 @RequestBody 对象字段校验异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResult<Void>> handleRequestBodyValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        return validationFailed(message);
    }

    /**
     * 处理 Controller 方法参数校验异常，
     * 例如 @RequestParam 上的 @Min、@Max。
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<CommonResult<Void>> handleMethodValidation(
            HandlerMethodValidationException exception
    ) {
        String message = exception.getParameterValidationResults()
                .stream()
                .flatMap(result ->
                        result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining("; "));

        return validationFailed(message);
    }

    /**
     * 兼容通过 Bean Validation 代理触发的约束异常。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResult<Void>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        return validationFailed(message);
    }

    /**
     * 兜底处理未预料的异常。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResult<Void>> handleUnknownException(
            Exception exception
    ) {
        LOGGER.error("Unhandled exception", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResult.failed("服务器内部错误"));
    }

    private ResponseEntity<CommonResult<Void>> validationFailed(
            String message
    ) {
        String finalMessage =
                message == null || message.isBlank()
                        ? "参数校验失败"
                        : message;

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResult.validateFailed(finalMessage));
    }
}