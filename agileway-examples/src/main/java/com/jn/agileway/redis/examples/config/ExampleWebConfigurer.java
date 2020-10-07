package com.jn.agileway.redis.examples.config;


import com.jn.agileway.springboot.web.rest.EnableGlobalRestHandlers;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableGlobalRestHandlers
public class ExampleWebConfigurer  implements WebMvcConfigurer {

}
