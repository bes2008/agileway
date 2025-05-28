package com.jn.agileway.httpclient.soap.entity;

import java.net.URI;
import java.util.Map;

public interface SoapFault {
    Map<String, String> getDetail();

    String getCode();

    String getSubCode();

    String getReason();

    URI getRole();
}
