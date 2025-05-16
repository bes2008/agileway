package com.jn.agileway.distributed.distributed.session;

import com.jn.langx.repository.Repository;

/**
 * @since 3.7.0
 */
public interface SessionRepository extends Repository<Session,String> {
    @Override
    Session getById(String id);

    @Override
    void add(Session entity);

    @Override
    void update(Session entity);

    @Override
    void removeById(String id);
}
