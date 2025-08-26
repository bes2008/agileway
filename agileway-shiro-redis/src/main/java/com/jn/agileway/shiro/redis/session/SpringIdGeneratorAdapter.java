package com.jn.agileway.shiro.redis.session;

import com.jn.langx.IdGenerator;

public class SpringIdGeneratorAdapter<E> implements IdGenerator<E> {
    private org.springframework.util.IdGenerator uuidGenerator;

    public SpringIdGeneratorAdapter(org.springframework.util.IdGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public String get(E e) {
        return get();
    }

    @Override
    public String get() {
        return uuidGenerator.generateId().toString();
    }
}
