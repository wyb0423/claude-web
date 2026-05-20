package com.claude.web.util;

import com.alibaba.fastjson.JSON;
import com.claude.web.constant.AuthConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shaoshuai
 * @date 2023-11-08
 */
@Component
@Slf4j
public class HttpUtil {
    public static final String CHARSET_UTF8 = "UTF-8";
    private static final HttpClientBuilder HTTP_CLIENT_BUILDER;
    private static final HttpClientBuilder HTTP_CLIENT_BUILDER2;

    static {
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(500);
        connMgr.setDefaultMaxPerRoute(500);

        RequestConfig requestConfig = RequestConfig.custom()
                // 从连接池获取连接的超时时间
                .setConnectionRequestTimeout(20000)
                // 读数据超时
                .setSocketTimeout(20000)
                // 连接建立时间
                .setConnectTimeout(3000)
                .build();
        // HttpClientBuilder 默认会使用重试机制（3次）
        HTTP_CLIENT_BUILDER = HttpClients.custom()
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig);

        RequestConfig requestConfig2 = RequestConfig.custom()
                // 从连接池获取连接的超时时间
                .setConnectionRequestTimeout(10000)
                // 读数据超时
                .setSocketTimeout(1000)
                // 连接建立时间
                .setConnectTimeout(3000)
                .build();

        HTTP_CLIENT_BUILDER2 = HttpClients.custom()
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig2);
    }

    private static CloseableHttpClient getHttpClient() {
        return HTTP_CLIENT_BUILDER.build();
    }

    public static String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("Accept-Charsets", StandardCharsets.UTF_8.toString());
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        try {
            CloseableHttpClient httpClient = getHttpClient();
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            return toString(httpResponse);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String url,String token) {
        CloseableHttpClient client = getHttpClient();

        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("Accept-Charsets", StandardCharsets.UTF_8.toString());
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        httpGet.setHeader("Authorization",token);
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            return toString(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //返回值处理和连接
    public static String toString(CloseableHttpResponse httpResponse) throws IOException {
        if (null == httpResponse) {
            return null;
        }

        try {
            //状态码错误，返回
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("request remote error statusCode:{}, reasonPhrase:{}",
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                return null;
            }

            final HttpEntity entity = httpResponse.getEntity();
            if (null != entity) {
                //如果长度未知，不建议使用EntityUtils读取entity
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }
        } finally {
            httpResponse.close(); //放回连接池
        }

        return null;
    }


    public static String post(String url, Map<String, Object> params) {
        String result = StringUtils.EMPTY;

        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpClient = getHttpClient();
        List<BasicNameValuePair> nameValuePairList = new ArrayList<>();

        for (Map.Entry<String, Object> param : params.entrySet()) {
            nameValuePairList.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, Charset.defaultCharset()));

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            result = EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            log.error("http post failed!", e);
        }

        return result;
    }

    public static String post2num(String url, Map<String, String> params) {
        String result = StringUtils.EMPTY;

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "*/*");
        httpPost.setHeader("Accept-Charsets", StandardCharsets.UTF_8.toString());
        httpPost.setHeader("Connection", "keep-alive");
        CloseableHttpClient httpClient = HTTP_CLIENT_BUILDER2.build();
        List<BasicNameValuePair> nameValuePairList = new ArrayList<>();

        for (Map.Entry<String, String> param : params.entrySet()) {
            nameValuePairList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, Charset.defaultCharset()));

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            result = EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            try{
                HttpResponse httpResponse = httpClient.execute(httpPost);
                result = EntityUtils.toString(httpResponse.getEntity());
            }catch (Exception e2){
                try{
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    result = EntityUtils.toString(httpResponse.getEntity());
                }catch (Exception e3){
                    log.error("http post failed 3次!", e3);

                }
                log.error("http post failed 2次!", e2);

            }
            log.error("http post failed 1次!", e);
        }

        return result;
    }


    public static HttpResponse postRawJson(String url, Map<String, String> params, Header header) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader(header);
        StringEntity stringEntity = new StringEntity(JSON.toJSONString(params), Charset.defaultCharset());
        httpPost.setEntity(stringEntity);
        CloseableHttpClient httpClient = getHttpClient();
        return httpClient.execute(httpPost);
    }

    public static String postRawJsonByToken(String url, String strJson, String userId, String token) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader(AuthConstant.USER_ID, userId);
        httpPost.setHeader(AuthConstant.APP_TOKEN_NAME, token);
        StringEntity stringEntity = new StringEntity(strJson, Charset.defaultCharset());
        httpPost.setEntity(stringEntity);
        return postRawJson(url, strJson, httpPost);
    }
    public static String postRawJsonByToken(String url, String strJson, String token) {
        return postRawJsonByToken(url, strJson, null, token);
    }

    public static String postRawJson(String url, String strJson, HttpPost httpPost) {
        String result = StringUtils.EMPTY;
        try {
            CloseableHttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
            } else {
                // 释放连接，否则一旦出现非200的连接，这个连接将永远将死在连接池里头
                log.info("http response is not 200, the connection will be aborted. url={}", url);
                httpPost.abort();
            }
        } catch (SocketTimeoutException e) {
            log.error("http post time out！ text:{}", strJson);
        } catch (Exception e) {
            log.error("http post failed! text:{}", strJson, e);
        }

        return result;
    }

    public static String postRawJson(String url, String strJson){
        String result = StringUtils.EMPTY;

        HttpPost httpPost = new HttpPost(url);
        // 👇 新增：设置自定义请求配置（覆盖默认配置）
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(30000)  // 从连接池获取连接超时：30秒
                .setSocketTimeout(60000)             // 读数据超时：60秒（关键！）
                .setConnectTimeout(5000)             // 建立连接超时：5秒
                .build();
        httpPost.setConfig(requestConfig);

        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept-Charsets", StandardCharsets.UTF_8.toString());
        httpPost.setHeader("Connection", "keep-alive");
        StringEntity stringEntity = new StringEntity(strJson, Charset.defaultCharset());
        httpPost.setEntity(stringEntity);

        try {
            CloseableHttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
            } else {
                // 释放连接，否则一旦出现非200的连接，这个连接将永远将死在连接池里头
                log.info("http response is not 200, the connection will be aborted. url={}", url);
                httpPost.abort();
            }
        } catch (SocketTimeoutException e) {
            log.error("http post time out！ text:{}", e.getMessage());
        } catch (Exception e) {
            log.error("http post failed! text:{}" , e.getMessage());
        }

        return result;
    }


    public static String postRawJsonSkipAuth(String url, String strJson){
        String result = StringUtils.EMPTY;

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept-Charsets", StandardCharsets.UTF_8.toString());
        httpPost.setHeader("Connection", "keep-alive");
        StringEntity stringEntity = new StringEntity(strJson, Charset.defaultCharset());
        httpPost.setEntity(stringEntity);
        try {

            SSLContextBuilder sslContextBuilder = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (x509Certificates, s) -> true);
            SSLContext sslContext =sslContextBuilder.build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    new String[]{"TLSv1.1", "SSLv3", "TLSv1", "TLSv1.2"},
                    null, NoopHostnameVerifier.INSTANCE
            );

            //CloseableHttpClient httpClient = getHttpClient();
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
            } else {
                // 释放连接，否则一旦出现非200的连接，这个连接将永远将死在连接池里头
                log.info("http response is not 200, the connection will be aborted. url={}", url);
                httpPost.abort();
            }
        } catch (SocketTimeoutException e) {
            log.error("http post time out！ text:{}", strJson);
        } catch (Exception e) {
            log.error("http post failed! text:{}" , strJson, e);
        }

        return result;
    }

    public static String getIpAddr(HttpServletRequest request) {
        //处理代理访问获取不到真正的ip问题的
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("PRoxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /***
     * 发送POST请求
     * @param url  地址
     * @param jsonStr json字符串  请求体里面的内容
     * @param headList 请求头设置token map集合
     *             headMap.put("key","Token");
     *             headMap.put("value","12345");
     */
    public static String setHttpRequest(String url,String jsonStr,List<Map<String,Object>> headList){
        HttpPost post = new HttpPost(url);
        log.info("url--"+url);
        try {
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept-Charsets", StandardCharsets.UTF_8.toString());
            post.setHeader("Connection", "keep-alive");
            if(jsonStr != null){
                StringEntity stringEntity = new StringEntity(jsonStr, Charset.defaultCharset());
                post.setEntity(stringEntity);
            }
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (x509Certificates, s) -> true);
            SSLContext sslContext =sslContextBuilder.build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    new String[]{"TLSv1.1", "SSLv3", "TLSv1", "TLSv1.2"},
                    null, NoopHostnameVerifier.INSTANCE
            );

            CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            if(headList != null){
                for(Map<String,Object> map : headList){
                    log.info("key----"+map.get("key"));
                    log.info("value----"+map.get("value"));
                    post.addHeader(map.get("key").toString(),map.get("value").toString());
                }
            }
            //启动执行请求，并获得返回值
            CloseableHttpResponse response = client.execute(post);
            //得到返回的entity对象
            HttpEntity entity = response.getEntity();
            //把实体对象转换为string
            //返回内容
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();

        }
        return null;
    }


    public static String sendPostRequestForJson(String urlString, String requestBody) {
        return sendPostRequest(urlString, requestBody, new HashMap<String, String>(2) {{
            put("Content-Type", "application/json");
            String token = TokenHolder.getToken();
            if (StringUtils.isNotBlank(token)) {
                put("app_token", token);
            }
        }});
    }
    /**
     * 通用请求方法post-非https
     * @param urlString 请求地址
     * @param requestBody json 或者 1&2
     * @param headers Content-Type: application/x-www-form-urlencoded application/json
     */
    public static String sendPostRequest(String urlString, String requestBody, Map<String, String> headers) {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(1000*10); // 超时时间

            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 写入请求体
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(requestBody);
            writer.flush();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            outputStream.close();
            reader.close();
        } catch (IOException e) {
            log.info("IOException=", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }
    public static String combineUrlList(String soaUrl, String soaCode, List<String> paramList, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("username", userId);
        map.put("funcno", soaCode);
        for (int i = 0; i < paramList.size(); i++) {
            map.put("$" + (i + 1), paramList.get(i));
        }
        String jwtToken = JwtTokenUtil.createSoaJWT(map);
        return soaUrl + "?jwt_token=" + jwtToken;
    }
}
