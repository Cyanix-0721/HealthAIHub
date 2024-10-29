package com.mole.health.exception;

import com.mole.health.result.ResultCode;

public class Asserts {

    // API异常相关方法
    public static void failApi(String message) {
        throw new ApiException(message);
    }

    public static void failApi(ResultCode errorCode) {
        throw new ApiException(errorCode);
    }

    public static void failApi(String message, ResultCode errorCode) {
        throw new ApiException(message, errorCode);
    }

    // 业务异常相关方法
    public static void failBusiness(String message) {
        throw new BusinessException(message);
    }

    public static void failBusiness(ResultCode errorCode) {
        throw new BusinessException(errorCode);
    }

    public static void failBusiness(String message, ResultCode errorCode) {
        throw new BusinessException(message, errorCode);
    }
}
