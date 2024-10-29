package com.mole.health.exception;

import com.mole.health.result.CommonResult;
import com.mole.health.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e BusinessException 异常实例
     * @return 包含错误码或错误信息的 CommonResult
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public CommonResult<?> handleBusinessException(BusinessException e) {
        log.error("Business exception occurred", e);
        if (e.getResultCode() != null) {
            return CommonResult.failed(e.getResultCode(), e.getMessage());
        }
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 处理自定义API异常
     *
     * @param e ApiException 异常实例
     * @return 包含错误码或错误信息的 CommonResult
     */
    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult<?> handleApiException(ApiException e) {
        log.error("Api exception occurred", e);
        if (e.getResultCode() != null) {
            return CommonResult.failed(e.getResultCode(), e.getMessage());
        }
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 处理方法参数验证异常
     *
     * @param e MethodArgumentNotValidException 异常实例
     * @return 包含验证错误信息的 CommonResult
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<?> handleValidException(MethodArgumentNotValidException e) {
        log.error("Method argument not valid exception occurred", e);
        return getCommonResult(e.getBindingResult());
    }

    /**
     * 处理绑定异常
     *
     * @param e BindException 异常实例
     * @return 包含验证错误信息的 CommonResult
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public CommonResult<?> handleValidException(BindException e) {
        log.error("Bind exception occurred", e);
        return getCommonResult(e.getBindingResult());
    }

    /**
     * 获取通用结果
     *
     * @param bindingResult 绑定结果
     * @return 包含验证错误信息的 CommonResult
     */
    private CommonResult<?> getCommonResult(BindingResult bindingResult) {
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理所有异常
     *
     * @param e 异常实例
     * @return 包含错误码或错误信息的 CommonResult
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);
        return CommonResult.failed(ResultCode.FAILED, "服务器内部错误");
    }
}
