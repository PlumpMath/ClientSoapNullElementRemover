package com.jaxws.ext.handlers;

import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ClientSoapWSSEUsernameTokenHandler implements SOAPHandler<SOAPMessageContext> {
  private String username;
  private String password;

  public ClientSoapWSSEUsernameTokenHandler() {
    this.username = "";
    this.password = "";
  }

  public ClientSoapWSSEUsernameTokenHandler( String _username, String _password ) {
    this.username = _username;
    this.password = _password;
  }

  public void setUsername( String _username ) {
    this.username = _username;
  }

  public void setPassword( String _password ) {
    this.password = _password;
  }

  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

    if (outboundProperty.booleanValue()) {
      try {
        SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
        SOAPFactory factory = SOAPFactory.newInstance();
        String prefix = "wsse";
        String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

        SOAPElement securityElem = factory.createElement("Security", prefix, uri);
        SOAPElement tokenElem = factory.createElement("UsernameToken", prefix, uri);
        tokenElem.addAttribute(QName.valueOf("wsu:Id"), "UsernameToken-2");
        tokenElem.addAttribute(QName.valueOf("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

        SOAPElement userElem = factory.createElement("Username", prefix, uri);
        userElem.addTextNode(this.username);

        SOAPElement pwdElem = factory.createElement("Password", prefix, uri);
        pwdElem.addTextNode(this.password);
        pwdElem.addAttribute(QName.valueOf("Type"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
        tokenElem.addChildElement(userElem);
        tokenElem.addChildElement(pwdElem);
        securityElem.addChildElement(tokenElem);

        SOAPHeader header = envelope.addHeader();
        header.addChildElement(securityElem);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return true;
  }

  @Override
  public Set<QName> getHeaders() {
    return new TreeSet();
  }

  @Override
  public boolean handleFault(SOAPMessageContext context) {
    return false;
  }

  @Override
  public void close(MessageContext context) {}
}
