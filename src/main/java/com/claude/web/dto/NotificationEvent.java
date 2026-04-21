package com.claude.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationEvent {
    
    private String method;
    private Object params;
    private String atIso;
    
    public NotificationEvent() {}
    
    public NotificationEvent(String method, Object params) {
        this.method = method;
        this.params = params;
        this.atIso = Instant.now().toString();
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
    
    public String getAtIso() {
        return atIso;
    }
    
    public void setAtIso(String atIso) {
        this.atIso = atIso;
    }
}
