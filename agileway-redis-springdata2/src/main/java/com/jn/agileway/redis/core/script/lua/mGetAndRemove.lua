-- @return list
local ks = unpack(KEYS[1])
local values = redis.call("MGET", ks)
redis.call("DEL", ks)
return values
