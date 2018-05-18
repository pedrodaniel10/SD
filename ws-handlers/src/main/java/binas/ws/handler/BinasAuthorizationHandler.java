package binas.ws.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.NodeList;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {	
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
		
		if (outboundElement.booleanValue()) {
			System.out.println("BinasAuthorizationHandler: Writing header to OUTbound SOAP message...");
			
			//nothing to do
		} 
		else {
			System.out.println("BinasAuthorizationHandler: Reading header from INbound SOAP message...");		
			try{
				//search email
				NodeList tagListEmails = smc.getMessage().getSOAPBody().getElementsByTagName("email");
				
				
				if(tagListEmails.getLength() == 1){
					tagListEmails.item(0).equals(smc.get("email"));
				}
				else if(tagListEmails.getLength() > 1){
					throw new RuntimeException("More than one email in request.");
				}
				
			} catch (SOAPException e) {
				throw new RuntimeException("Problem getting soap.");
			}
		}

		
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
		
}
