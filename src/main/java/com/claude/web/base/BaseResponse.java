package com.claude.web.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.claude.web.constant.ReturnCode;
import lombok.Data;

/**
 * @author shaoshuai
 * @date 2023-11-08
 */
@Data
public class BaseResponse {
    @JSONField(name = "msg")
    protected String msg = ReturnCode.SUCCESS.getText();
    @JSONField(name = "code")
    protected int code = ReturnCode.SUCCESS.getCode();

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(ReturnCode codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getText();
    }
}
