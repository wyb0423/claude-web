package com.claude.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ClaudeWebApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ClaudeWebApplication.class, args);
    }
}
