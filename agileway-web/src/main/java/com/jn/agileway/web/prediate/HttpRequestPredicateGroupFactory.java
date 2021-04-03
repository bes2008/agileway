package com.jn.agileway.web.prediate;

import com.jn.langx.factory.Factory;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestPredicateGroupFactory implements Factory<HttpRequestPredicateConfigItems, HttpRequestPredicateGroup> {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestPredicateGroupFactory.class);

    @Override
    public final HttpRequestPredicateGroup get(final HttpRequestPredicateConfigItems configItems) {
        final HttpRequestPredicateGroup group = new HttpRequestPredicateGroup();
        if (Objs.isEmpty(configItems)) {
            return group;
        }
        Collects.forEach(configItems, new Consumer<HttpRequestPredicateConfigItem>() {
            @Override
            public void accept(HttpRequestPredicateConfigItem configItem) {
                HttpRequestPredicate predicate = null;

                logger.info("parse http-request-predicate: {}", configItem);
                String key = configItem.getKey();

                HttpRequestPredicateFactory factory = HttpRequestPredicateFactoryRegistry.getInstance().get(key);

                if (factory == null) {
                    logger.warn("Can't find a http-request-predicate factory for {}", key);
                } else {
                    String configuration = configItem.getConfiguration();
                    if (Strings.isNotBlank(configuration)) {
                        predicate = factory.get(configuration);
                    }
                }

                if (predicate != null) {
                    group.add(predicate);
                }
            }
        });
        return group;
    }

}
