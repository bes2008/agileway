package com.jn.agileway.httpclient.soap.utils;

import com.jn.agileway.httpclient.soap.entity.*;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.IOs;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SOAPs {
    private SOAPs() {
    }

    public static final String NAMESPACE_PREFIX_DEFAULT = "agilewaysoap";
    private static String soapEnvelopeTemplate = "<${namespacePrefix}:Envelope xmlns:agilewaysoap=\"${namespaceUri}\">" + Strings.CRLF +
            "    <${namespacePrefix}:Header>" + Strings.CRLF +
            "${header}" +
            "    </${namespacePrefix}:Header>" + Strings.CRLF +
            "    <${namespacePrefix}:Body>" + Strings.CRLF +
            "${payload}" +
            "    </${namespacePrefix}:Body>" + Strings.CRLF +
            "</${namespacePrefix}:Envelope>" + Strings.CRLF;

    private static String marshalSoapHeader(String headerAttrNamespacePrefix, SoapVersion soapVersion, SoapHeader soapHeader) {
        if (soapHeader == null) {
            return Strings.CRLF;
        }
        if (Objs.isEmpty(soapHeader.getElements())) {
            return Strings.CRLF;
        } else {
            headerAttrNamespacePrefix = Strings.isBlank(headerAttrNamespacePrefix) ? NAMESPACE_PREFIX_DEFAULT : headerAttrNamespacePrefix;
            StringBuilder builder = new StringBuilder();
            for (SoapHeaderElement element : soapHeader.getElements()) {
                String elementNamespacePrefix = element.getName().getPrefix();
                String elementTag = Strings.isEmpty(elementNamespacePrefix) ? element.getName().getLocalPart() : (elementNamespacePrefix + ":" + element.getName().getLocalPart());

                // xmlns
                String namespaceAttrName = null;
                String namespaceAttrValue = element.getName().getNamespaceURI();
                if (Strings.isNotEmpty(namespaceAttrValue)) {
                    namespaceAttrName = Strings.isEmpty(elementNamespacePrefix) ? "xmlns" : ("xmlns:" + element.getName().getPrefix());
                }

                builder.append("        ").append("<").append(elementTag);
                if (namespaceAttrName != null) {
                    builder.append(" ").append(namespaceAttrName).append("=\"").append(namespaceAttrValue).append("\"");
                }

                // mustUnderstand
                builder.append(" ")
                        .append(headerAttrNamespacePrefix)
                        .append(":")
                        .append("mustUnderstand=\"")
                        .append(element.isMustUnderstand())
                        .append("\"");

                // role, actor
                if (Strings.isNotEmpty(element.getRole())) {
                    String roleAttrName = soapVersion == SoapVersion.V1_2 ? "role" : "actor";
                    builder.append(" ")
                            .append(headerAttrNamespacePrefix)
                            .append(":")
                            .append(roleAttrName)
                            .append("=\"").append(element.getRole()).append("\"");
                }

                // relay
                if (soapVersion == SoapVersion.V1_2) {
                    builder.append(" ")
                            .append(headerAttrNamespacePrefix)
                            .append(":")
                            .append("relay=\"")
                            .append(element.isRelay())
                            .append("\"");
                }

                builder.append(" >").append(Strings.CRLF);
                builder.append("        ").append("</").append(elementTag).append(">").append(Strings.CRLF);
            }
            return builder.toString();
        }
    }

    public static String marshalSoapEnvelope(Object soapPayload) throws Throwable {
        return marshalSoapEnvelope(null, null, null, soapPayload);
    }

    public static String marshalSoapEnvelope(SoapEnvelope soapEnvelope) throws Throwable {
        return marshalSoapEnvelope(null, soapEnvelope);
    }

    public static String marshalSoapEnvelope(String namespacePrefix, SoapEnvelope soapEnvelope) throws Throwable {
        return marshalSoapEnvelope(namespacePrefix, soapEnvelope.getVersion(), soapEnvelope.getHeader(), soapEnvelope.getBody());
    }

    public static String marshalSoapEnvelope(String namespacePrefix, SoapVersion soapVersion, SoapHeader soapHeader, SoapBody soapBody) throws Throwable {
        return marshalSoapEnvelope(namespacePrefix, soapVersion, soapHeader, soapBody == null ? null : soapBody.getPayload());
    }

    public static String marshalSoapEnvelope(String namespacePrefix, SoapVersion soapVersion, SoapHeader soapHeader, Object soapPayload) throws Throwable {
        if (soapPayload instanceof SoapEnvelope) {
            soapPayload = ((SoapEnvelope) soapPayload).getBody().getPayload();
            if (soapHeader == null) {
                soapHeader = ((SoapEnvelope) soapPayload).getHeader();
            }
            if (soapVersion == null) {
                soapVersion = ((SoapEnvelope) soapPayload).getVersion();
            }
        }
        if (soapPayload instanceof SoapBody) {
            soapPayload = ((SoapBody) soapPayload).getPayload();
        }
        if (soapPayload == null) {
            throw new NullPointerException("soap payload is required");
        }
        if (soapVersion == null) {
            soapVersion = SoapVersion.V1_2;
        }
        namespacePrefix = Strings.isBlank(namespacePrefix) ? NAMESPACE_PREFIX_DEFAULT : namespacePrefix;
        String header = marshalSoapHeader(namespacePrefix, soapVersion, soapHeader);

        // payload
        byte[] bytes = JAXBs.marshal(soapPayload);
        List<String> lines = IOs.readLines(new ByteArrayInputStream(bytes));
        StringBuilder payloadBuilder = new StringBuilder();

        boolean first = true;
        for (String line : lines) {
            if (first) {
                if (Strings.isNotBlank(line)) {
                    line = line.trim();
                    if (line.startsWith("<?xml")) {
                        // ignore it
                    } else {
                        payloadBuilder.append("        ").append(line).append(Strings.CRLF);
                    }
                    first = false;
                }
            } else {
                payloadBuilder.append("        ").append(line).append(Strings.CRLF);
            }
        }

        Map<String, String> soapEnvelopeTemplateVariables = new HashMap<String, String>();
        soapEnvelopeTemplateVariables.put("namespaceUri", soapVersion.getNamespaceUri());
        soapEnvelopeTemplateVariables.put("header", header);
        soapEnvelopeTemplateVariables.put("payload", payloadBuilder.toString());
        soapEnvelopeTemplateVariables.put("namespacePrefix", namespacePrefix);
        String soapEnvelope = StringTemplates.formatWithMap(soapEnvelopeTemplate, soapEnvelopeTemplateVariables);
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
