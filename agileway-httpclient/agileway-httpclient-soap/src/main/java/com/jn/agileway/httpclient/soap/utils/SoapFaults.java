package com.jn.agileway.httpclient.soap.utils;

import com.jn.agileway.httpclient.soap.entity.SoapFault;
import com.jn.agileway.httpclient.soap.exception.MalformedSoapMessageException;
import com.jn.langx.util.Strings;

public class SoapFaults {
    public static SoapFault unmarshalSoapFaultV11(String soapEnvelopeXml) throws Exception {
        if (Strings.isBlank(soapEnvelopeXml)) {
            throw new MalformedSoapMessageException("illegal soap envelope: it is blank");
        }
        String soapPayloadXml = SOAPs.extractSoapPayloadXml(soapEnvelopeXml);
        if (Strings.isBlank(soapPayloadXml)) {
            throw new MalformedSoapMessageException("illegal soap body payload: it is blank");
        }
        SoapFault soapFault = new SoapFault();

        return soapFault;
    }

    public static SoapFault unmarshalSoapFaultV12(String soapEnvelopeXml) throws Exception {
        if (Strings.isBlank(soapEnvelopeXml)) {
            throw new MalformedSoapMessageException("illegal soap envelope: it is blank");
        }
        String soapPayloadXml = SOAPs.extractSoapPayloadXml(soapEnvelopeXml);
        if (Strings.isBlank(soapPayloadXml)) {
            throw new MalformedSoapMessageException("illegal soap body payload: it is blank");
        }
        SoapFault soapFault = new SoapFault();
        return soapFault;
    }
}
