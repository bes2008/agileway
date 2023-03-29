package com.jn.agileway.springboot.appboot;

import com.jn.agileway.spring.utils.SpringContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAppAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public SpringContextHolder springContextHolder(){
        return new SpringContextHolder();
    }
}
