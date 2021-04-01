package com.jn.agileway.web.filter.waf.xss;

import com.jn.agileway.web.filter.waf.WAFStrategy;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.EmptyEvalutible;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class XssFirewall implements Initializable, EmptyEvalutible {
    private boolean inited = false;
    private boolean enabled = false;
    private final List<WAFStrategy> strategies = Collects.emptyArrayList();


    @Override
    public void init() throws InitializationException {
        if (!inited) {
            Collects.forEach(strategies, new Consumer<WAFStrategy>() {
                @Override
                public void accept(WAFStrategy strategy) {
                    strategy.init();
                }
            });
            inited = true;
        }
    }

    public WAFStrategy findStrategy(final RR holder) {
        WAFStrategy strategy = Collects.findFirst(strategies, new Predicate<WAFStrategy>() {
            @Override
            public boolean test(WAFStrategy strategy) {
                return strategy.match(holder);
            }
        });
        return strategy;
    }

    @Override
    public boolean isEmpty() {
        return strategies.isEmpty();
    }

    public void addStrategy(WAFStrategy strategy) {
        if (strategy != null) {
            this.strategies.add(strategy);
        }
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

