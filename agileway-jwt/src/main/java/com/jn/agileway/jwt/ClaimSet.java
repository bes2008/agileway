package com.jn.agileway.jwt;


import com.jn.langx.Accessor;

import java.util.Map;

public interface ClaimSet extends Accessor<String, Map<String,Object>> {
    Map<String,Object> getAllClaims();
}
