package com.claude.web.base;


import com.claude.web.constant.ReturnCode;
import lombok.Data;

@Data
public class CommonException extends RuntimeException {
    private Integer code;
    private String message;

    public CommonException(ReturnCode returnCode) {
        this.code = returnCode.getCode();
        this.message = returnCode.getText();
    }

    public CommonException(ReturnCode returnCode, String message) {
        this.code = returnCode.getCode();
        this.message = message;
    }

    public CommonException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
