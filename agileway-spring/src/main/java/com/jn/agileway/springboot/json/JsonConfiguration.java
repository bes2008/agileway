package com.jn.agileway.springboot.json;

import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(JSONFactory.class)
public class JsonConfiguration {
    @Bean
    @ConditionalOnMissingBean({JSONFactory.class})
    public JSONFactory jsonFactory() {
        return JsonFactorys.getJSONFactory(JsonScope.SINGLETON);
    }

}
