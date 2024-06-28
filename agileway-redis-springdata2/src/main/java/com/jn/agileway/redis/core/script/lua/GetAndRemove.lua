-- @return
local value = redis.call("get", KEYS[1])
if value then
    redis.call("DEL", KEYS[1])
    return value
end
