package com.claude.web.util;

import com.claude.web.base.CommonException;
import com.claude.web.config.JwtConfig;
import com.claude.web.constant.ReturnCode;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

/**
 * @author shaoshuai
 * @date 2023-12-22
 */
@Slf4j
@Configuration
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String base64Secret;

    public static final String USER_ID = "userId";
    public static final String BIND_IP = "bindIp";

    public static JwtConfig jwtConfig;

    @PostConstruct
    public void init(){
        jwtConfig = new JwtConfig(base64Secret);
    }

    /**
     * 构建jwt
     * @param userId 用户ID
     * @return 返回结果
     */
    public static String createJwt(String userId) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(USER_ID, userId);
        try {
            //使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            //当前时间
            long nowMillis = System.currentTimeMillis();

            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtConfig.getBase64Secret());
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            //添加构成JWT的参数
            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                    // 可以将基本不重要的对象信息放到claims
                    .setClaims(params)
                    // 代表这个JWT的签发主体；
                    .setIssuer(jwtConfig.getClientId())
                    // 是一个时间戳，代表这个JWT的签发时间；
                    .setIssuedAt(new Date())
                    // 代表这个JWT的接收对象；
//                    .setAudience(jwtConfig.getName())
                    .signWith(signatureAlgorithm, signingKey);
            //添加Token过期时间
            int ttlMillis = jwtConfig.getExpiresSecond() * 1000;
            if (ttlMillis >= 0) {
                long expMillis = nowMillis + ttlMillis;
                Date exp = new Date(expMillis);
                //时间戳，代表这个JWT的过期时间；
                builder.setExpiration(exp);
            }
            //生成JWT
            return builder.compact();
        } catch (Exception e) {
            log.error("构建jwt异常,{}", e.getMessage());
            throw new CommonException(ReturnCode.TOKEN_CREATE_ERROR);
        }
    }

    /**
     * 解析jwt
     * @param token 参数
     * @param base64Secret 秘钥
     * @return 返回结果
     */
    public static Claims parseJwt(String token, String base64Secret) {
        try {
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Secret))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException eje) {
            log.error("token过期,{}", eje.getMessage());
            throw new CommonException(ReturnCode.TOKEN_INVALID);
        } catch (Exception e) {
            log.error("解析jwt异常,{}", e.getMessage());
            throw new CommonException(ReturnCode.TOKEN_ERROR);
        }
    }

    public static String getUserId(String token) {
        Claims claims= parseJwt(token, jwtConfig.getBase64Secret());
        return claims.get(USER_ID).toString();
    }

    public static Claims getUserLoginInfo(String token) {
        return parseJwt(token, jwtConfig.getBase64Secret());
    }

    /**
     * 构建SOA Token
     */
    public static String createSoaJWT(Map<String,String> map) {
        Map<String, Object> params = new HashMap<>();
        map.forEach(params::put);
        try {
            // 使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter
                    .parseBase64Binary(jwtConfig.getSoaBase64Secret());
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            //添加构成JWT的参数
            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                    // 可以将基本不重要的对象信息放到claims
                    .setClaims(params)
                    // 代表这个JWT的签发主体；
                    .setIssuer(jwtConfig.getClientId())
                    // 是一个时间戳，代表这个JWT的签发时间；
                    .setIssuedAt(new Date())
                    // 代表这个JWT的接收对象；
                    .signWith(signatureAlgorithm, signingKey);
            //添加Token过期时间
            int TTLMillis = jwtConfig.getShortExpiresSecond() * 1000;
            if (TTLMillis >= 0) {
                long expMillis = nowMillis + TTLMillis;
                Date exp = new Date(expMillis);
                // 是一个时间戳，代表这个JWT的过期时间；
                builder.setExpiration(exp);
            }
            //生成JWT
            return builder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new CommonException(ReturnCode.TOKEN_CREATE_ERROR);
        }
    }
}
