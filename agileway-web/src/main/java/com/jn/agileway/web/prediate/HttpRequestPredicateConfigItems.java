package com.jn.agileway.web.prediate;

import com.jn.langx.util.EmptyEvalutible;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;

import java.util.Iterator;
import java.util.List;

import static com.jn.agileway.web.prediate.HttpRequestPredicates.PREDICATE_CONFIGURATION_ITEM_SEPARATOR;

/**
 * 这里面的每一个字段，都代表一个类限制条件。如果为 null ，则代表无此项限制。
 * 如果所有字段都是 null，就代表没有任何的限制，也就是任何请求都匹配的。
 * <p>
 * 针对这个类，建议的正确使用方式是，要启用自动化条件配置时，
 */

public class HttpRequestPredicateConfigItems implements Iterable<HttpRequestPredicateConfigItem>, EmptyEvalutible {

    /**
     * 每一个元素，都是 key=config 表达式
     */
    private String[] predicates;

    public String[] getPredicates() {
        return predicates;
    }

    public void setPredicates(String[] predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean isEmpty() {
        return Objs.isEmpty(predicates);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public Iterator<HttpRequestPredicateConfigItem> iterator() {
        List<HttpRequestPredicateConfigItem> items = Pipeline.of(predicates).filter(new Predicate<String>() {
            @Override
            public boolean test(String itemExpression) {
                if (Strings.isBlank(itemExpression)) {
                    return false;
                }
                int keyValueSeparatorIndex = itemExpression.indexOf(PREDICATE_CONFIGURATION_ITEM_SEPARATOR);
                if (keyValueSeparatorIndex == -1 || keyValueSeparatorIndex == 0 || keyValueSeparatorIndex == itemExpression.length() - 1) {
                    return false;
                }
                return true;
            }
        }).map(new Function<String, HttpRequestPredicateConfigItem>() {
            @Override
            public HttpRequestPredicateConfigItem apply(String itemExpression) {
                int keyValueSeparatorIndex = itemExpression.indexOf(PREDICATE_CONFIGURATION_ITEM_SEPARATOR);
                String key = itemExpression.substring(0, keyValueSeparatorIndex);
                String configuration = itemExpression.substring(keyValueSeparatorIndex + PREDICATE_CONFIGURATION_ITEM_SEPARATOR.length());
                HttpRequestPredicateConfigItem item = new HttpRequestPredicateConfigItem();
                item.setKey(key);
                item.setConfiguration(configuration);
                return item;
            }
        }).asList();
        return items.iterator();
    }
}
