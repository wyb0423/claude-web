package com.claude.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.claude.web.annotations.PassToken;
import com.claude.web.base.BaseResponse;
import com.claude.web.base.CommonException;
import com.claude.web.constant.Constant;
import com.claude.web.constant.ReturnCode;
import com.claude.web.util.JwtTokenUtil;
import com.claude.web.util.TokenHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;


/**
 * @author wbjiaopj
 */
@Component
@Slf4j
public class TokenAuthorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        log.info("requestInfo = {}, {}, {}, {}, {}"
                , request.getCharacterEncoding()
                , request.getMethod()
                , request.getRequestURL().toString()
                , request.getContentLengthLong() + ""
                , handlerMethod.getShortLogMessage()
        );
        //检查是否有PassToken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            return true;
        }
        // 吹了url=/error的请求，直接跳过认证
        if ("/error".equals(request.getRequestURI())) {
            return true;
        }

        String token = request.getHeader(Constant.TOKEN);
        //请求头部Authorization为空跳出
        if (StringUtils.isBlank(token)) {
            log.info("getPathInfo={}", request.getRequestURI());
            log.error("请求头缺失token", new CommonException(ReturnCode.NO_TOKEN));
            returnJson(response, new BaseResponse(ReturnCode.NO_TOKEN));
            return false;
        }

        //获取token中的userId
        String userId = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(token)) {
            Claims claims = JwtTokenUtil.getUserLoginInfo(token);
            userId = claims.get(JwtTokenUtil.USER_ID).toString();
            if (StringUtils.isBlank(userId)) {
                log.error("token中的userId为空", new CommonException(ReturnCode.USER_INFO_ERROR));
                returnJson(response, new BaseResponse(ReturnCode.USER_INFO_ERROR));
                return false;
            }
        }
        TokenHolder.setToken(token);
        log.info("curLoginUserId = {}", userId);
        request.setAttribute("userId", userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TokenHolder.removeToken();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    /**
     * resp转json
     *
     * @param response HttpServletResponse
     * @param data     数据
     */
    private void returnJson(HttpServletResponse response, BaseResponse data) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JSON.toJSONString(data));
        } catch (IOException e) {
            log.error("response error", e);
        }
    }

}
