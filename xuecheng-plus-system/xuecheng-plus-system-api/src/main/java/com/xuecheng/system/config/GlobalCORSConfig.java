package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author Linzkr
 * @description: TODO 解决浏览器的跨域问题
 * @date 2023/1/14 20:02
 */
@Configuration
public class GlobalCORSConfig {

    @Bean
    public CorsFilter getCorsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        添加哪些http方法可以跨域 比如 GET POST 多个方法中间以逗号分割
        corsConfiguration.addAllowedMethod("*");
//        添加哪些源请求进行跨域 可以具体指定哪些url可以跨域
        corsConfiguration.addAllowedOrigin("*");
//        添加加了某个头信息的可以放行
        corsConfiguration.addAllowedHeader("*");
//        允许跨域发送cookie
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        指定哪些URL需要跨域
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
