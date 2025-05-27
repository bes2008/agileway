package com.jn.agileway.httpclient.soap.utils;

import com.jn.agileway.httpclient.soap.entity.*;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayInputStream;
import java.util.List;

public class SOAPs {
    private SOAPs() {
    }

    private static String soapEnvelopeTemplate = "<agilewaysoap:Envelope xmlns:agilewaysoap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + Strings.CRLF +
            "{}" +
            "    <agilewaysoap:Body>" + Strings.CRLF +
            "{}" +
            "    </agilewaysoap:Body>" + Strings.CRLF +
            "</agilewaysoap:Envelope>" + Strings.CRLF;

    public static String marshalSoapHeader(SoapHeader soapHeader) {
        if (soapHeader == null) {
            return "    <agilewaysoap:Header/>" + Strings.CRLF;
        }
        if (Objs.isEmpty(soapHeader.getElements())) {
            return "    <agilewaysoap:Header/>" + Strings.CRLF;
        } else {
            StringBuilder builder = new StringBuilder();
            for (SoapHeaderElement element : soapHeader.getElements()) {
                String elementNamespacePrefix = element.getName().getPrefix();
                String elementTag = Strings.isEmpty(elementNamespacePrefix) ? element.getName().getLocalPart() : (elementNamespacePrefix + ":" + element.getName().getLocalPart());
                String namespaceAttrName = null;
                String namespaceAttrValue = element.getName().getNamespaceURI();
                if (Strings.isNotEmpty(namespaceAttrValue)) {
                    namespaceAttrName = Strings.isEmpty(elementNamespacePrefix) ? "xmlns" : ("xmlns:" + element.getName().getPrefix());
                }

                builder.append("        ").append("<").append(elementTag);
                if (namespaceAttrName != null) {
                    builder.append(" ").append(namespaceAttrName).append("=\"").append(namespaceAttrValue).append("\"");
                }
                if (Strings.isNotEmpty(element.getRole())) {
                    if ("1.2".equals(soapHeader.getVersion())) {
                        builder.append(" agilewaysoap:role=\"").append(element.getRole()).append("\"");
                    } else {
                        builder.append(" agilewaysoap:actor=\"").append(element.getActor()).append("\"");
                    }
                }
                builder.append(" agilewaysoap:mustUnderstand=\"").append("" + element.isMustUnderstand()).append("\"");
                if ("1.2".equals(soapHeader.getVersion())) {
                    builder.append(" agilewaysoap:relay=\"").append("" + element.isRelay()).append("\"");
                }

                builder.append(" >").append(Strings.CRLF);
                builder.append("        ").append("</").append(elementTag).append(">").append(Strings.CRLF);
            }
            return builder.toString();
        }
    }

    public static String marshalSoapEnvelope(Object soapPayload) throws Throwable {
        return marshalSoapEnvelope(null, soapPayload);
    }

    public static String marshalSoapEnvelope(SoapEnvelope soapEnvelope) throws Throwable {
        return marshalSoapEnvelope(soapEnvelope.getHeader(), soapEnvelope.getBody());
    }

    public static String marshalSoapEnvelope(SoapHeader soapHeader, SoapBody soapBody) throws Throwable {
        return marshalSoapEnvelope(soapHeader, soapBody == null ? null : soapBody.getPayload());
    }

    public static String marshalSoapEnvelope(SoapHeader soapHeader, Object soapPayload) throws Throwable {
        if (soapPayload instanceof SoapEnvelope) {
            soapPayload = ((SoapEnvelope) soapPayload).getBody().getPayload();
            soapHeader = ((SoapEnvelope) soapPayload).getHeader();
        }
        if (soapPayload instanceof SoapBody) {
            soapPayload = ((SoapBody) soapPayload).getPayload();
        }
        if (soapPayload == null) {
            throw new NullPointerException("soap payload is required");
        }
        String header = marshalSoapHeader(soapHeader);
        byte[] bytes = JAXBs.marshal(soapPayload);
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

        String soapEnvelope = StringTemplates.formatWithPlaceholder(soapEnvelopeTemplate, header, builder.toString());
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

    public static <T> T unmarshalSoapPayload(String soapEnvelopeXml, Class<T> expectedClazz) throws Exception {
        if (Strings.isBlank(soapEnvelopeXml)) {
            throw new RuntimeException("illegal soap envelope: it is blank");
        }
        String soapPayloadXml = extractSoapPayloadXml(soapEnvelopeXml);
        if (Strings.isBlank(soapPayloadXml)) {
            throw new RuntimeException("illegal soap body payload: it is blank");
        }
        return JAXBs.unmarshal(soapPayloadXml, expectedClazz);
    }

    public static SoapFault unmarshalSoapFault(String soapEnvelopeXml) throws Exception {
        if (Strings.isBlank(soapEnvelopeXml)) {
            throw new RuntimeException("illegal soap envelope: it is blank");
        }
        String soapPayloadXml = extractSoapPayloadXml(soapEnvelopeXml);
        if (Strings.isBlank(soapPayloadXml)) {
            throw new RuntimeException("illegal soap body payload: it is blank");
        }
        // return JAXBs.unmarshal(soapPayloadXml, expectedClazz);
        SoapFault soapFault = new SoapFault();
        return soapFault;
    }

}
