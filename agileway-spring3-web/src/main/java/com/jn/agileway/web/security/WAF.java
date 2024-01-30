package com.jn.agileway.web.security;

import com.jn.agileway.http.rr.RR;
import com.jn.langx.Nameable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.EmptyEvalutible;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public abstract class WAF implements Initializable, EmptyEvalutible, Nameable {
    private boolean inited = false;
    private String name;
    private final List<WAFStrategy> strategies = Collects.emptyArrayList();

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return this.name;
    }

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

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);
}
