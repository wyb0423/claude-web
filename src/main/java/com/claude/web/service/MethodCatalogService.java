package com.claude.web.service;

import com.claude.web.config.ClaudeProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class MethodCatalogService {
    
    private static final Logger logger = LoggerFactory.getLogger(MethodCatalogService.class);
    
    private final AppServerProcess appServerProcess;
    private final ObjectMapper objectMapper;
    
    private List<String> methodCache;
    private List<String> notificationCache;
    
    public MethodCatalogService(AppServerProcess appServerProcess, ObjectMapper objectMapper) {
        this.appServerProcess = appServerProcess;
        this.objectMapper = objectMapper;
    }
    
    public synchronized List<String> listMethods() throws Exception {
        if (methodCache != null) {
            return methodCache;
        }
        
        // Get schema from remote app-server via RPC
        Object result = appServerProcess.rpc("system/getSchema", null);
        String schemaJson = objectMapper.writeValueAsString(result);
        JsonNode root = objectMapper.readTree(schemaJson);
        
        Set<String> methods = extractMethodsFromSchema(root);
        methodCache = new ArrayList<>(methods);
        return methodCache;
    }
    
    public synchronized List<String> listNotificationMethods() throws Exception {
        if (notificationCache != null) {
            return notificationCache;
        }
        
        // Get schema from remote app-server via RPC
        Object result = appServerProcess.rpc("system/getSchema", null);
        String schemaJson = objectMapper.writeValueAsString(result);
        JsonNode root = objectMapper.readTree(schemaJson);
        
        Set<String> methods = extractNotificationsFromSchema(root);
        notificationCache = new ArrayList<>(methods);
        return notificationCache;
    }
    
    private Set<String> extractMethodsFromSchema(JsonNode root) {
        Set<String> methods = new TreeSet<>();
        
        // Try to extract from ClientRequest schema
        JsonNode clientRequest = root.get("ClientRequest");
        if (clientRequest != null) {
            JsonNode oneOf = clientRequest.get("oneOf");
            if (oneOf != null && oneOf.isArray()) {
                for (JsonNode entry : oneOf) {
                    JsonNode properties = entry.get("properties");
                    if (properties != null) {
                        JsonNode methodDef = properties.get("method");
                        if (methodDef != null) {
                            JsonNode methodEnum = methodDef.get("enum");
                            if (methodEnum != null && methodEnum.isArray()) {
                                for (JsonNode item : methodEnum) {
                                    if (item.isTextual()) {
                                        methods.add(item.asText());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return methods;
    }
    
    private Set<String> extractNotificationsFromSchema(JsonNode root) {
        Set<String> methods = new TreeSet<>();
        
        // Try to extract from ServerNotification schema
        JsonNode serverNotification = root.get("ServerNotification");
        if (serverNotification != null) {
            JsonNode oneOf = serverNotification.get("oneOf");
            if (oneOf != null && oneOf.isArray()) {
                for (JsonNode entry : oneOf) {
                    JsonNode properties = entry.get("properties");
                    if (properties != null) {
                        JsonNode methodDef = properties.get("method");
                        if (methodDef != null) {
                            JsonNode methodEnum = methodDef.get("enum");
                            if (methodEnum != null && methodEnum.isArray()) {
                                for (JsonNode item : methodEnum) {
                                    if (item.isTextual()) {
                                        methods.add(item.asText());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return methods;
    }
}
