-- @return Object
local value = redis.call('get', KEYS[1])
if value == false then
    redis.call('set', KEYS[1], ARGV[1])
    redis.call('expire', KEYS[1], ARGV[2])
    return ARGV[1]
else
    redis.call('expire', KEYS[1], ARGV[2])
    return value
end