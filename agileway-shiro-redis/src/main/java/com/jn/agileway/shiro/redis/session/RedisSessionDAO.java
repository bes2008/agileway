package com.jn.agileway.shiro.redis.session;

import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.agileway.redis.core.key.RedisKeyWrapper;
import com.jn.langx.IdGenerator;
import com.jn.langx.annotation.NonNull;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class RedisSessionDAO extends AbstractSessionDAO {
    private static final Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
    private RedisTemplate<String, Session> redisTemplate;

    private RedisKeyWrapper sessionKeyWrapper = new RedisKeyWrapper().prefix("shiro:session");

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = generateSessionId(session);
        this.assignSessionId(session, sessionId);
        saveToRedis(session);
        return sessionId;
    }

    @Override
    protected void assignSessionId(Session session, Serializable sessionId) {
        if(session instanceof SimpleSession) {
            ((SimpleSession)session).setId(sessionId);
        }
        else if(session instanceof SimpleShiroSession){
            ((SimpleShiroSession)session).setId(sessionId);
        }
    }

    private String getSessionIdRedisKey(Serializable sessionId) {
        if (sessionId == null) {
            logger.error("sessionId is null");
            throw new UnknownSessionException("session is null");
        }
        return sessionKeyWrapper.wrap(sessionId.toString());
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String sessionIdRedisKey = getSessionIdRedisKey(sessionId);
        Session session = redisTemplate.opsForValue().get(sessionIdRedisKey);
        if (session != null) {
            if (session.getLastAccessTime().getTime() + session.getTimeout() < System.currentTimeMillis()) {
                delete(session);
                return null;
            }
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        saveToRedis(session);
    }

    private void saveToRedis(@NonNull Session session) {
        Serializable sessionId = session.getId();
        String sessionIdRedisKey = getSessionIdRedisKey(sessionId);
        long ttl = session.getTimeout();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(sessionIdRedisKey, session, ttl, TimeUnit.MILLISECONDS);
        } else if (ttl == 0L) {
            this.delete(session);
        } else {
            // never timeout
            redisTemplate.opsForValue().set(sessionIdRedisKey, session);
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = session.getId();
        String sessionIdRedisKey = getSessionIdRedisKey(sessionId);
        redisTemplate.delete(sessionIdRedisKey);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }

    public void setSessionIdGenerator(IdGenerator sessionIdGenerator) {
        setSessionIdGenerator(new SessionIdGeneratorAdapter(sessionIdGenerator));
    }

    public void setSessionIdGenerator(org.springframework.util.IdGenerator uuidGenerator) {
        setSessionIdGenerator(new SessionIdGeneratorAdapter(uuidGenerator));
    }

    public void setRedisTemplate(RedisTemplate<String, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setSessionKeyWrapper(RedisKeyWrapper sessionKeyWrapper) {
        this.sessionKeyWrapper = sessionKeyWrapper;
    }
}
