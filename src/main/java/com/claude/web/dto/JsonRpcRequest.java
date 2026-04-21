package com.claude.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcRequest {
    
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("params")
    private Object params;
    
    public JsonRpcRequest() {}
    
    public JsonRpcRequest(Integer id, String method, Object params) {
        this.id = id;
        this.method = method;
        this.params = params;
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
