package com.zkh.ex.config;

import com.zkh.constant.BusinessEnum;
import com.zkh.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalConsumerExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Result.fail(BusinessEnum.SERVER_INNER_ERROR);
    }
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return Result.fail(BusinessEnum.OPTION_FAIL);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage(), e);
        throw e;
    }

}
