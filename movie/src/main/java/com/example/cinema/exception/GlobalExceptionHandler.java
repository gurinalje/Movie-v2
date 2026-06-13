package com.example.cinema.exception;

import com.example.cinema.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一拦截未被捕获的异常，返回标准错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO handleBusinessException(BusinessException e) {
        logger.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseVO.buildFailure(e.getMessage());
    }

    /**
     * 处理所有其他未预期的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return ResponseVO.buildFailure("系统繁忙，请稍后重试");
    }
}
