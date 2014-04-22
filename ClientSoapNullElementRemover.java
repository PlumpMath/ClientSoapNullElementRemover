package com.jaxws.ext.handlers;

import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import javax.annotation.Resource;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ClientSoapNullElementRemover implements SOAPHandler<SOAPMessageContext> {
  String element_name;

  public ClientSoapNullElementRemover( String _element_name ) {
    this.element_name = _element_name;
  }

  public void setElementName( String _element_name ) {
    this.element_name = _element_name;
  }

  private String xpathExpression() {
    return "//*[local-name() = '" + this.element_name.trim() + "'][@*[local-name() = 'nil'] = 'true']";
  }

  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

    if (outboundProperty.booleanValue()) {
      try {
        // Get the SOAP Envelope
        final SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xPath = xpathFactory.newXPath();
        NodeList removableNodes = (NodeList) xPath.evaluate(xpathExpression(), envelope, XPathConstants.NODESET); 

        for(int i = 0; i < removableNodes.getLength(); i++) {
          org.w3c.dom.Node current_node = removableNodes.item(i);
          org.w3c.dom.Name parent_node = current_node.getParentNode()
          
          if (parent_node != null) {
            parent_node.removeChild(current_node);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return true;
  }

  @Override
  public Set<QName> getHeaders() {
    final HashSet headers = new HashSet();
    return headers;
  }

  @Override
  public boolean handleFault(SOAPMessageContext context) {
    return false;
  }

  @Override
  public void close(MessageContext context) {}
}
