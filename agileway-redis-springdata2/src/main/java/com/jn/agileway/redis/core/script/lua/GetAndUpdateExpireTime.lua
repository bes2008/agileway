-- getAndUpdateExpireTime(key, expireMills)
-- eval "return $getAndUpdateExpireTime" 1 $key $expireMills
local value = redis.call("get", KEYS[1])
if value then
    redis.call("PEXPIRE", KEYS[1], ARGV[1])
    return value
end