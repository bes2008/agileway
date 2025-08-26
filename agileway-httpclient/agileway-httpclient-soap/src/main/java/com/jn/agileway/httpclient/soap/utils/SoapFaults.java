package com.jn.agileway.httpclient.soap.utils;

import com.jn.agileway.httpclient.soap.entity.SoapBinding;
import com.jn.agileway.httpclient.soap.entity.SoapFault;
import com.jn.agileway.httpclient.soap.exception.MalformedSoapMessageException;
import com.jn.langx.text.xml.Namespaces;
import com.jn.langx.text.xml.XmlAccessor;
import com.jn.langx.text.xml.Xmls;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SoapFaults {
    /**
     * @param document the soap envelope document
     * @return
     * @throws Exception
     * @see <a href="https://www.ibm.com/docs/en/integration-bus/10.0?topic=message-soap-fault">SOAP Fault</a>
     */
    public static SoapFault unmarshalSoapFaultV11(Document document) throws Exception {
        String documentNamespacePrefix = Namespaces.getDocumentRootNamespace(document);
        Element faultElement = new XmlAccessor(documentNamespacePrefix).getElement(document, "/Envelope/Body/Fault");

        Map<String, Object> soapFaultMap = new HashMap<String, Object>();
        xmlElementChildrenToMap(faultElement.getChildNodes(), soapFaultMap);


        SoapFault soapFault = new SoapFault();

        String faultCode = (String) soapFaultMap.get("faultcode");
        if (Strings.isBlank(faultCode)) {
            throw new MalformedSoapMessageException("illegal soap fault: <faultcode/> is required");
        }
        if (faultCode.startsWith(documentNamespacePrefix + ":")) {
            soapFault.setSubCode(faultCode.substring(documentNamespacePrefix.length() + 1));
        }
        soapFault.setCode(faultCode);

        String faultstring = (String) soapFaultMap.get("faultstring");
        if (Strings.isBlank(faultstring)) {
            throw new MalformedSoapMessageException("illegal soap fault: <faultstring/> is required");
        }
        soapFault.setReason(faultstring);

        String faultActor = (String) soapFaultMap.get("faultactor");
        if (Strings.isNotBlank(faultActor)) {
            soapFault.setRole(URI.create(faultActor));
        }

        Object detail = soapFaultMap.get("detail");
        if (detail instanceof Map) {
            soapFault.setDetail((Map) detail);
        }

        return soapFault;
    }

    public static SoapFault unmarshalSoapFault(String soapEnvelopeXml) throws Exception {
        if (Strings.isBlank(soapEnvelopeXml)) {
            throw new MalformedSoapMessageException("malformed soap envelope: it is blank");
        }

        Document document = Xmls.getXmlDoc(new ByteArrayInputStream(soapEnvelopeXml.getBytes(Charsets.UTF_8)));

        String namespaceUri = document.getDocumentElement().getNamespaceURI();
        if (Strings.isBlank(namespaceUri)) {
            throw new MalformedSoapMessageException("the namespace of a soap message is required");
        }

        SoapBinding soapBinding = SoapBinding.getByNamespaceUri(namespaceUri);
        if (soapBinding == null) {
            throw new MalformedSoapMessageException("the namespace of a soap message is not supported: " + namespaceUri);
        }
        if (soapBinding.equals(SoapBinding.SOAP11_HTTP)) {
            return unmarshalSoapFaultV11(document);
        }
        return unmarshalSoapFaultV12(document);
    }

    public static SoapFault unmarshalSoapFaultV12(Document document) throws Exception {

        String documentNamespacePrefix = Namespaces.getDocumentRootNamespace(document);
        Element faultElement = new XmlAccessor(documentNamespacePrefix).getElement(document, "/Envelope/Body/Fault");

        Map<String, Object> soapFaultMap = new HashMap<String, Object>();
        xmlElementChildrenToMap(faultElement.getChildNodes(), soapFaultMap);

        SoapFault soapFault = new SoapFault();

        // /Envelope/Body/Fault/Code
        if (!soapFaultMap.containsKey("Code")) {
            throw new MalformedSoapMessageException("malformed soap fault: /Envelope/Body/Fault/Code is required");
        }
        Map<String, Object> faultCode = (Map<String, Object>) soapFaultMap.get("Code");
        // /Envelope/Body/Fault/Code/Value
        if (!faultCode.containsKey("Value")) {
            throw new MalformedSoapMessageException("malformed soap fault: /Envelope/Body/Fault/Code/Value is required");
        }
        String faultCodeValue = (String) faultCode.get("Value");
        if (faultCodeValue.startsWith(documentNamespacePrefix + ":")) {
            soapFault.setCode(faultCodeValue.substring(documentNamespacePrefix.length() + 1));
        } else {
            soapFault.setCode(faultCodeValue);
        }
        // /Envelope/Body/Fault/Code/Subcode
        if (faultCode.containsKey("Subcode")) {
            soapFault.setSubCode((String) faultCode.get("Subcode"));
        }

        // /Envelope/Body/Fault/Reason
        if (!soapFaultMap.containsKey("Reason")) {
            throw new MalformedSoapMessageException("malformed soap fault: /Envelope/Body/Fault/Reason is required");
        }

        Map<String, Object> faultReason = (Map<String, Object>) soapFaultMap.get("Reason");
        if (!faultReason.containsKey("Text")) {
            throw new MalformedSoapMessageException("malformed soap fault: /Envelope/Body/Fault/Reason/Text is required");
        }
        String faultReasonText = (String) faultReason.get("Text");
        soapFault.setReason(faultReasonText);

        String faultRole = (String) soapFaultMap.get("Role");
        if (Strings.isNotBlank(faultRole)) {
            soapFault.setRole(URI.create(faultRole));
        }

        String faultNode = (String) soapFaultMap.get("Node");
        if (Strings.isNotBlank(faultNode)) {
            soapFault.setNode(URI.create(faultNode));
        }

        Object detail = soapFaultMap.get("Detail");
        if (detail instanceof Map) {
            soapFault.setDetail((Map) detail);
        }

        return soapFault;
    }

    private static void xmlElementChildrenToMap(NodeList children, Map<String, Object> map) {
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            boolean hasChildElements = false;
            for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                Node childNode = element.getChildNodes().item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    hasChildElements = true;
                    break;
                }
            }
            if (hasChildElements) {
                Map<String, Object> value = new HashMap<String, Object>();
                xmlElementChildrenToMap(element.getChildNodes(), value);
                map.put(element.getLocalName(), value);
            } else {
                map.put(element.getLocalName(), element.getTextContent());
            }
        }
    }
}
