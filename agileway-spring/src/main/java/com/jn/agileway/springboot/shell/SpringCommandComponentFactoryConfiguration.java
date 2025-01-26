package com.jn.agileway.springboot.shell;

import com.jn.agileway.shell.exec.CommandComponentFactory;
import com.jn.agileway.spring.shell.SpringComponentFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(CommandComponentFactory.class)
@Configuration
public class SpringCommandComponentFactoryConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public SpringComponentFactory springCommandComponentFactory(){
        return new SpringComponentFactory();
    }
}
