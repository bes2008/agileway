package com.jn.agileway.httpclient.soap.utils;

import com.jn.agileway.httpclient.soap.entity.*;
import com.jn.agileway.httpclient.soap.exception.MalformedSoapMessageException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.text.xml.Namespaces;
import com.jn.langx.text.xml.Xmls;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SOAPs {
    private SOAPs() {
    }

    public static final String NAMESPACE_PREFIX_DEFAULT = "agilewaysoap";
    private static String soapEnvelopeTemplate = "<?xml version='1.0' encoding='UTF-8' ?>" + Strings.CRLF +
            "<${namespacePrefix}:Envelope xmlns:${namespacePrefix}=\"${namespaceUri}\">" + Strings.CRLF +
            "    <${namespacePrefix}:Header>" + Strings.CRLF +
            "${header}" +
            "    </${namespacePrefix}:Header>" + Strings.CRLF +
            "    <${namespacePrefix}:Body>" + Strings.CRLF +
            "${payload}" +
            "    </${namespacePrefix}:Body>" + Strings.CRLF +
            "</${namespacePrefix}:Envelope>" + Strings.CRLF;

    public static String marshalSoapEnvelope(Object obj) throws Throwable {
        if (obj instanceof SoapMessage) {
            return marshalSoapEnvelope((SoapMessage) obj);
        }
        if (obj instanceof SoapEnvelope) {
            return marshalSoapEnvelope(null, (SoapEnvelope) obj);
        }
        if (obj instanceof SoapHeader) {
            throw new MalformedSoapMessageException("soap body is required");
        }
        if (obj instanceof SoapBody) {
            SoapEnvelope soapEnvelope = new SoapEnvelope();
            soapEnvelope.setBody((SoapBody) obj);
            return marshalSoapEnvelope(null, soapEnvelope);
        }
        if (obj instanceof SOAPMessage) {
            SOAPMessage soapMessage = (SOAPMessage) obj;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            soapMessage.writeTo(bao);
            return new String(bao.toByteArray(), Charsets.UTF_8);
        }

        SoapEnvelope soapEnvelope = new SoapEnvelope();
        soapEnvelope.setBody(new SoapBody(obj));
        return marshalSoapEnvelope(null, soapEnvelope);
    }

    public static String marshalSoapEnvelope(SoapMessage soapMessage) throws Exception {
        return marshalSoapEnvelope(soapMessage.getMetadata(), soapMessage.getEnvelope());
    }

    public static String marshalSoapEnvelope(SoapMessageMetadata metadata, SoapEnvelope soapEnvelope) throws Exception {
        Object soapPayload = soapEnvelope.getBody().getPayload();
        if (soapPayload == null) {
            throw new MalformedSoapMessageException("soap payload is required");
        }

        if (metadata == null) {
            metadata = new SoapMessageMetadata();
        }
        String header = marshalSoapHeader(metadata, soapEnvelope.getHeader());

        String payload = marshalSoapPayload(soapPayload);

        Map<String, String> soapEnvelopeTemplateVariables = new HashMap<String, String>();
        soapEnvelopeTemplateVariables.put("namespaceUri", metadata.getBinding().getNamespaceUri());
        soapEnvelopeTemplateVariables.put("header", header);
        soapEnvelopeTemplateVariables.put("payload", payload);
        soapEnvelopeTemplateVariables.put("namespacePrefix", metadata.getNamespacePrefix());
        String soapEnvelopeXml = StringTemplates.formatWithMap(soapEnvelopeTemplate, soapEnvelopeTemplateVariables);
        return soapEnvelopeXml;
    }

    private static String marshalSoapHeader(SoapMessageMetadata metadata, SoapHeader soapHeader) {
        if (soapHeader == null) {
            return Strings.CRLF;
        }

        if (Objs.isEmpty(soapHeader.getElements())) {
            return Strings.CRLF;
        } else {
            String headerAttrNamespacePrefix = metadata.getNamespacePrefix();
            SoapBinding soapVersion = metadata.getBinding();

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
                // SOAP 1.1 用 0,1 表示
                // SOAP 1.2 用 false,true 表示
                String mustUnderstandValue = "" + element.isMustUnderstand();
                if (soapVersion == SoapBinding.SOAP11_HTTP) {
                    mustUnderstandValue = element.isMustUnderstand() ? "1" : "0";
                }

                builder.append(" ")
                        .append(headerAttrNamespacePrefix)
                        .append(":")
                        .append("mustUnderstand=\"")
                        .append(mustUnderstandValue)
                        .append("\"");

                // role, actor
                if (element.getRole() != null) {
                    String roleAttrName = soapVersion == SoapBinding.SOAP12_HTTP ? "role" : "actor";
                    builder.append(" ")
                            .append(headerAttrNamespacePrefix)
                            .append(":")
                            .append(roleAttrName)
                            .append("=\"").append(element.getRole()).append("\"");
                }

                // relay, 只有 SOAP 1.2 才有
                if (soapVersion == SoapBinding.SOAP12_HTTP) {
                    builder.append(" ")
                            .append(headerAttrNamespacePrefix)
                            .append(":")
                            .append("relay=\"")
                            .append(element.isRelay())
                            .append("\"");
                }

                builder.append(" >").append(Strings.CRLF);


                for (Map.Entry<String, String> property : element.getPropertySet().entrySet()) {
                    String propertyTag = Strings.isEmpty(elementNamespacePrefix) ? property.getKey() : (elementNamespacePrefix + ":" + property.getKey());
                    builder.append("            ").append("<").append(propertyTag).append(">").append(property.getValue()).append("</").append(propertyTag).append(">").append(Strings.CRLF);
                }
                builder.append("        ").append("</").append(elementTag).append(">").append(Strings.CRLF);
            }
            return builder.toString();
        }
    }


    private static String marshalSoapPayload(Object soapPayload) throws Exception {
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
        return payloadBuilder.toString();
    }

    /**
     * 获取 <soap:Body> 节点下的内容
     *
     * @param soapEnvelopeXml the soap envelope , in xml format
     * @return the soap payload
     */
    public static String extractSoapPayloadXml(String soapEnvelopeXml) throws Exception {
        Document document = Xmls.getXmlDoc(new ByteArrayInputStream(soapEnvelopeXml.getBytes(Charsets.UTF_8)));
        Element root = document.getDocumentElement();
        if (!Strings.equals("Envelope", root.getLocalName())) {
            throw new RuntimeException("invalid soap envelope");
        }
        String namespacePrefix = Namespaces.getDocumentRootNamespace(document);
        String bodyElementTag = namespacePrefix + ":Body";

        String bodyElementStartFlag = "<" + bodyElementTag;
        String bodyElementEndFlag = "</" + bodyElementTag + ">";
        int startIndex = soapEnvelopeXml.indexOf(bodyElementStartFlag);
        int endIndex = soapEnvelopeXml.indexOf(bodyElementEndFlag, startIndex);
        if (startIndex == -1 || endIndex == -1) {
            throw new MalformedSoapMessageException("invalid soap envelope, missing /Envelope/Body");
        }

        String payloadXml = soapEnvelopeXml.substring(startIndex, endIndex);
        startIndex = payloadXml.indexOf(">");
        if (startIndex != -1) {
            throw new MalformedSoapMessageException("invalid soap envelope, missing /Envelope/Body");
        }
        payloadXml = payloadXml.substring(startIndex + 1);
        return payloadXml;
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


}
