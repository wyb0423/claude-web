package com.claude.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerRequestReply {
    
    private Object result;
    private JsonRpcError error;
    
    public static class JsonRpcError {
        private int code;
        private String message;
        
        public JsonRpcError() {}
        
        public JsonRpcError(int code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public JsonRpcError getError() {
        return error;
    }
    
    public void setError(JsonRpcError error) {
        this.error = error;
    }
}
