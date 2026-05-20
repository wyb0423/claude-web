package com.claude.web.util;

public class TokenHolder {

    private static final ThreadLocal<String> THREAD_LOCAL_TOKEN = new ThreadLocal<>();

    public static void setToken(String token) {
        THREAD_LOCAL_TOKEN.set(token);
    }

    public static String getToken() {
        return THREAD_LOCAL_TOKEN.get();
    }

    public static void removeToken() {
        THREAD_LOCAL_TOKEN.remove();
    }
}