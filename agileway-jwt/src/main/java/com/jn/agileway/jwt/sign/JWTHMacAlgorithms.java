package com.jn.agileway.jwt.sign;

import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;

import java.util.List;
import java.util.Map;

public class JWTHMacAlgorithms {
    static Map<String,String> jwtAlgorithmToHMac;
    static{
        Map<String,String>  map  = Maps.newLinkedHashMap();
        map.put("HS256","hmacsha256");
        map.put("HS384","hmacsha384");
        map.put("HS512","hmacsha512");
        jwtAlgorithmToHMac=map;
    }


}
