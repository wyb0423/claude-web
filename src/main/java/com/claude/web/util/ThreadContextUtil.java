package com.claude.web.util;

import com.claude.web.constant.Constant;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @Description 获取当前线程上下文信息
 * @Author LiuXin
 * @Date 2023-8-10
 */
public class ThreadContextUtil {

    /**
     * 获取当前线程请求信息
     */
    public static HttpServletRequest getRequst() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取当前线程请求token信息
     */
    public static String getThreadToken() {
        HttpServletRequest request = getRequst();
        if (request != null) {
            String hrToken = request.getHeader(Constant.TOKEN);
            if (StringUtils.isEmpty(hrToken)) {
                if (request.getAttribute(Constant.TOKEN) != null) {
                    hrToken = request.getAttribute(Constant.TOKEN).toString();
                }
            }
            return hrToken;
        }
        return null;
    }

    /**
     * 获取当前线程请求用户id信息
     */
    public static String getThreadUserId() {
        HttpServletRequest request = getRequst();
        if (request != null) {
            return (String) request.getAttribute(Constant.USERID);
        }
        return null;
    }

    /**
     * 获取当前线程请求用户id信息
     */
    public static void setAttributeToken(String hrToken) {
        HttpServletRequest request = getRequst();
        if (request != null) {
            request.setAttribute(Constant.TOKEN, hrToken);
        }
    }
}
