package com.claude.web.dto;

public class RpcProxyRequest {
    
    private String method;
    private Object params;
    
    public RpcProxyRequest() {}
    
    public RpcProxyRequest(String method, Object params) {
        this.method = method;
        this.params = params;
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
