package com.macro.mall.tiny.common.exception;

import com.macro.mall.tiny.common.api.IErrorCode;
import com.macro.mall.tiny.common.api.ResultCode;

public class ApiException extends RuntimeException {
    private final IErrorCode errorCode;

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public ApiException(IErrorCode errorCode, String message){
        super(message);
        this.errorCode=errorCode;
    }
    public ApiException(String message){
        super(message);
        this.errorCode= ResultCode.FAILED;
    }
    public IErrorCode getErrorCode(){
        return errorCode;
    }
}

