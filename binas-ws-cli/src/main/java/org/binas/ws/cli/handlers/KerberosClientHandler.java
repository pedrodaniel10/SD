package org.binas.ws.cli.handlers;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext> {

	private final String KERBEROS_WS_URL = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
	
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
		System.out.println("KerberosClientHandler: Handling message.");
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		try {
			if (outboundElement.booleanValue()) {
				System.out.println("KerberosClientHandler: Writing header to OUTbound SOAP message...");


			} 
			else {
				System.out.println("KerberosClientHandler: Reading header from INbound SOAP message...");


			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}
		
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
