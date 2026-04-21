package com.claude.web.dto;

public class PendingServerRequest {
    
    private int id;
    private String method;
    private Object params;
    private String receivedAtIso;
    
    public PendingServerRequest() {}
    
    public PendingServerRequest(int id, String method, Object params, String receivedAtIso) {
        this.id = id;
        this.method = method;
        this.params = params;
        this.receivedAtIso = receivedAtIso;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
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
    
    public String getReceivedAtIso() {
        return receivedAtIso;
    }
    
    public void setReceivedAtIso(String receivedAtIso) {
        this.receivedAtIso = receivedAtIso;
    }
}
