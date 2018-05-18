package binas.ws.handler;

import java.io.StringWriter;
import java.lang.reflect.Array;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.Cipher;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {
	private static final String XML_TARGET_NAMESPACE = "http://ws.binas.org/";
	private static final String TEST_PROP_FILE = "/secrets.properties";
	
	private static final String DIGEST_ALGO = "SHA-256";
	
	@Override
	public void close(MessageContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("MACHandler: Handling message.");
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		try {
			if (outboundElement.booleanValue()) {
				System.out.println("MACClientHandler: Writing header to OUTbound SOAP message...");
				
				//nothing to do
			} 
			else {
				System.out.println("MACHandler: Reading header from INbound SOAP message...");		
				
				//search email
				NodeList tagListEmails = smc.getMessage().getSOAPBody().getElementsByTagName("email");
				
				for(int i = 0; i < tagListEmails.getLength(); i++){
					if(!tagListEmails.item(i).equals(smc.get("email"))){
						throw new RuntimeException("Acessing data from another email.");
					}
				}
			}
				
		} 
			catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private SOAPElement getSoapElement(SOAPEnvelope envelope, SOAPHeader header, String elementName) throws SOAPException{
		javax.xml.soap.Name name = envelope.createName(elementName, "security", XML_TARGET_NAMESPACE);
		
		Iterator<SOAPElement> iterator = header.getChildElements(name);
		
		if(iterator.hasNext() == false){
			return null;
		}
		else{
			return (SOAPElement) iterator.next();
		}
	}
	
}
