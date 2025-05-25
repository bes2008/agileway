package com.jn.agileway.httpclient.soap.utils;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.IOs;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayInputStream;
import java.util.List;

public class SOAPs {
    private SOAPs() {
    }

    private static String soapEnvelopeTemplate = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + Strings.CRLF +
            "  <soap:Body>" + Strings.CRLF +
            "{}" +
            "  </soap:Body>" + Strings.CRLF +
            "</soap:Envelope>" + Strings.CRLF;


    public static String javaBeanToSoapEnvelope(Object soapPayload) throws Throwable {
        byte[] bytes = JAXBs.javaBeanToXml(soapPayload);
        List<String> lines = IOs.readLines(new ByteArrayInputStream(bytes));
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (String line : lines) {
            if (first) {
                if (Strings.isNotBlank(line)) {
                    line = line.trim();
                    if (line.startsWith("<?xml")) {
                        // ignore it
                    } else {
                        builder.append("    ").append(line).append(Strings.CRLF);
                    }
                    first = false;
                }
            } else {
                builder.append("    ").append(line).append(Strings.CRLF);
            }
        }

        String soapEnvelope = StringTemplates.formatWithPlaceholder(soapEnvelopeTemplate, builder.toString());
        return soapEnvelope;
    }

    /**
     * 获取 <soap:Body> 节点下的内容
     *
     * @param soapEnvelopeXml the soap envelope , in xml format
     * @return the soap payload
     */
    public static String extractSoapPayloadXml(String soapEnvelopeXml) throws Exception {
        Document document = DocumentHelper.parseText(soapEnvelopeXml);
        Element root = document.getRootElement();
        if (!Strings.equals("Envelope", root.getName())) {
            throw new RuntimeException("invalid soap envelope");
        }
        Element bodyElement = root.element("Body");
        if (bodyElement == null) {
            throw new RuntimeException("invalid soap envelope, missing body");
        }
        List<Element> children = bodyElement.elements();
        if (Objs.isNotEmpty(children)) {
            return children.get(0).asXML();
        }
        return "";
    }

}
