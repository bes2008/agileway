-- @usage: UnlockExclusiveLock([key] [expectedValue, force])
-- @param expectedValue {any}
--          the expectedValue value
-- @param force {boolean}
--          whether force unlock or not
-- @return boolean

local value = redis.call("get", KEYS[1])
if ARGV[2] then
    redis.call("del", KEYS[1])
    return true
else
    if value == ARGV[1] then
        redis.call("del", KEYS[1])
        return true
    else
        return false
    end
end
