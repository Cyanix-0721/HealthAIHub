package com.mole.health.exception;

import com.mole.health.result.ResultCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ResultCode resultCode;

    public ApiException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILED;
    }

    public ApiException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ApiException(String message, ResultCode resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.resultCode = ResultCode.FAILED;
    }

    public ApiException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.resultCode = resultCode;
    }
}
