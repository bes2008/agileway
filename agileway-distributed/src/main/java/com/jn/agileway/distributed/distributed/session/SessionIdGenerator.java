package com.jn.agileway.distributed.distributed.session;

import com.jn.langx.IdGenerator;

/**
 * @since 3.7.0
 * @param <E>
 */
public interface SessionIdGenerator<E> extends IdGenerator<E> {
    @Override
    String get(E e);
}
