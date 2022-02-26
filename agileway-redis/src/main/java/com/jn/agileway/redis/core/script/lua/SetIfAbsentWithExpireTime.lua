-- @usage: SetIfAbsentWithExpireTime([key1], [ttl, value])
-- @param ttl
--          the ttl expire time (mills)
-- @param value
--          the value of the key1
-- @return boolean
-- @since redis 2.6.0

local value = redis.call('get', KEYS[1])
if value == nil then
    redis.call('psetex', KEYS[1], ARGV[1], ARGV[2])
    return true
else
    return false
end

