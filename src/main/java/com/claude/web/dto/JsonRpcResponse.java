package com.claude.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcResponse {
    
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private JsonRpcError error;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("params")
    private Object params;
    
    public static class JsonRpcError {
        @JsonProperty("code")
        private int code;
        
        @JsonProperty("message")
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
    
    public String getJsonrpc() {
        return jsonrpc;
    }
    
    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Object getParams() {
        return params;
    }
    
    public void setParams(Object params) {
        this.params = params;
    }
}
