package com.jn.agileway.redis.examples.controller.redis_examples;

import com.jn.agileway.codec.serialization.json.EasyjsonCodec;
import com.jn.agileway.redis.core.RedisTemplate;
import com.jn.agileway.redis.core.RedisTemplates;
import com.jn.agileway.redis.core.script.RedisLuaScriptRepository;
import com.jn.agileway.redis.core.serialization.DelegatableRedisSerializer;
import com.jn.agileway.redis.locks.ExclusiveLock;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequestMapping("/redis-examples/string")
@RestController
public class StringValueController implements InitializingBean {


    private RedisConnectionFactory connectionFactory;
    private RedisLuaScriptRepository repository;

    @Value("${agileway.redis.global-template.key.prefix:agileway_redis_test}")
    private String keyPrefix;

    ExclusiveLock lock = new ExclusiveLock();

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        lock.setRedisTemplate(redisTemplate);
        lock.setResource("test_lock_key");
    }

    private RedisTemplate createRedisTemplate(TestScope scope) {
        RedisTemplate<String, ?> RedisTemplate = null;
        switch (scope) {
            case JACKSON_COMMON:
                RedisTemplate = RedisTemplates.createRedisTemplate(
                        connectionFactory,
                        keyPrefix,
                        new GenericJackson2JsonRedisSerializer(),
                        StringValueController.class.getClassLoader(),
                        RedisTemplates.stringRedisSerializer,
                        null,
                        null,
                        repository,
                        false, true);
                break;
            case EASYJSON_COMMON:
                EasyjsonCodec jsonSerializer = new EasyjsonCodec();
                DelegatableRedisSerializer valueSerializer = new DelegatableRedisSerializer(jsonSerializer);

                RedisTemplate = RedisTemplates.createRedisTemplate(
                        connectionFactory,
                        keyPrefix,
                        valueSerializer,
                        StringValueController.class.getClassLoader(),
                        RedisTemplates.stringRedisSerializer,
                        null,
                        valueSerializer,
                        repository,
                        false, true);
                break;
            default:
                RedisTemplate = RedisTemplates.createRedisTemplate(
                        connectionFactory,
                        keyPrefix,
                        new StringRedisSerializer(),
                        StringValueController.class.getClassLoader(),
                        RedisTemplates.stringRedisSerializer,
                        null,
                        null,
                        repository,
                        false, true);
                break;
        }
        return RedisTemplate;
    }

    @PostMapping("/set")
    public String set(@RequestParam String key, @RequestParam String value, @RequestParam TestScope testScope) {
        RedisTemplate redisTemplate = createRedisTemplate(testScope);
        BoundValueOperations<String, String> operations = redisTemplate.boundValueOps(key);
        operations.set(value);
        return operations.get();
    }


    @GetMapping("/get")
    public String get(@RequestParam String key, @RequestParam TestScope testScope) {
        RedisTemplate redisTemplate = createRedisTemplate(testScope);
        BoundValueOperations<String, String> operations = redisTemplate.boundValueOps(key);
        return operations.get();
    }


    @PostMapping("/setBean")
    public Object setBean(@RequestParam String key, @RequestParam String personName, @RequestParam int personAge, @RequestParam TestScope testScope) {
        RedisTemplate redisTemplate = createRedisTemplate(testScope);
        BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(key);
        Person p = new Person();
        p.setAge(personAge);
        p.setName(personName);
        operations.set(p);
        return operations.get();
    }


    @GetMapping("/getBean")
    public Object getBean(@RequestParam String key, @RequestParam TestScope testScope) {
        RedisTemplate redisTemplate = createRedisTemplate(testScope);
        BoundValueOperations<String, String> operations = redisTemplate.boundValueOps(key);
        return operations.get();
    }


    @GetMapping("/increment")
    public String increment(@RequestParam String key, @RequestParam int delta, @RequestParam TestScope testScope) {
        RedisTemplate redisTemplate = createRedisTemplate(testScope);
        BoundValueOperations<String, String> operations = redisTemplate.boundValueOps(key);
        return operations.increment(delta).toString();
    }


    @GetMapping("/decrement")
    public String decrement(@RequestParam String key, @RequestParam int delta, @RequestParam TestScope testScope) {
        RedisTemplate redisTemplate = createRedisTemplate(testScope);
        BoundValueOperations<String, String> operations = redisTemplate.boundValueOps(key);
        return operations.increment(-delta).toString();
    }

    @GetMapping("/keys")
    public Set<String> keys() {
        RedisTemplate redisTemplate = createRedisTemplate(TestScope.NORMAL_STRING);
        return redisTemplate.keys("*");
    }

    @GetMapping("/doWithLock")
    public String doWithLock(String key, String value) {
        try {
            lock.forceUnlock();
            lock.tryLock(50, TimeUnit.SECONDS);
            String old = get(key,TestScope.EASYJSON_COMMON);
            set(key,value,TestScope.EASYJSON_COMMON);
            return old;
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }finally {
            lock.unlock();
        }
    }

    @Autowired
    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
