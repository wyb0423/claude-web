package com.claude.web.config;

import com.claude.web.interceptor.TokenAuthorInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author shaoshuai
 * @date 2023-12-22
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public TokenAuthorInterceptor setBean(){
        return new TokenAuthorInterceptor();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 👈 所有路径都允许跨域
                .allowedOrigins("*") // 允许所有源（生产环境建议指定具体域名）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition") // 用于下载文件名称问题
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//       所有请求都不拦截
        String [] excludes = new String[]{"/**"};
//        拦截除了auth之外的所有
//        String [] excludes = new String[]{"/auth/**"};
        registry.addInterceptor(setBean()).excludePathPatterns(excludes)
                .excludePathPatterns("/favicon.ico")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/doc.html/**")
                .excludePathPatterns("/doc.html#/**")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/swagger-resources")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v2/api-docs")
                .excludePathPatterns("/v3/api-docs")
                .excludePathPatterns("/v3/api-docs/**");
    }
}
