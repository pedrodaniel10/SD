package binas.ws.handler;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext> {
	private static final String XML_TARGET_NAMESPACE = "http://ws.binas.org/";
	private static final String TEST_PROP_FILE = "/server.properties";
	private RequestTime requestTimeSend = null;
	private Key keyKcs = null;
	
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
		System.out.println("KerberosServerHandler: Handling message.");
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		if (outboundElement.booleanValue()) {
			try{
				System.out.println("KerberosServerHandler: Writing header to OUTbound SOAP message...");
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				//write to header
				SOAPHeader header = se.getHeader();
				
				if(header == null) {
					header = se.addHeader();
				}
								
				RequestTime requestTime = requestTimeSend;
				
				if(requestTime == null){
					throw new RuntimeException("No request time.");
				}
				
				//add reqTime
				javax.xml.soap.Name nameTimeReq = se.createName("requestTime", "security", XML_TARGET_NAMESPACE);
				SOAPHeaderElement elementTimeReq = header.addHeaderElement(nameTimeReq);
				
				String stringRequestTime = toBase64(requestTime.cipher(keyKcs));
				elementTimeReq.addTextNode(stringRequestTime);
			
			} catch (SOAPException e) {
				throw new RuntimeException("Problem getting soap.");
			} catch (KerbyException e) {
				throw new RuntimeException("Problem running Kerberos protocol.");
			}
			
		} 
		else {
			try{
				System.out.println("KerberosServerHandler: Reading header from INbound SOAP message...");
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				//write to header
				SOAPHeader header = se.getHeader();
				
				if(header == null) {
					throw new RuntimeException("No header to get ticket and auth.");
				}
				
				SOAPElement nodeTicket = getSoapElement(se, header, "ticket");
				SOAPElement nodeAuth = getSoapElement(se, header, "auth");
								
				if(nodeTicket == null || nodeAuth == null){
					throw new RuntimeException("Missing headers for security.");
				}
				else{
					String email = getServerEmail();
					String pass = getPassword();
					Key keyServer = getKey(pass);
					
					
					String ticketString = nodeTicket.getTextContent();
					CipheredView ticketView = fromBase64(ticketString);
	
					Ticket ticket = new Ticket(ticketView, keyServer);
					
					
					String authString = nodeAuth.getTextContent();
					CipheredView authView = fromBase64(authString);
							
					Auth auth = new Auth(authView, ticket.getKeyXY());
					
					//check client 
					if(!ticket.getX().equals(auth.getX())) {
						throw new RuntimeException("Client doesnt match");
					}
					
					smc.put("email", ticket.getX());
					
					//check treq between t1 and t2
					Date now = new Date();
					if(auth.getTimeRequest().before(ticket.getTime1()) ||
					   auth.getTimeRequest().after(ticket.getTime2()) ||
					   now.before(ticket.getTime1()) ||
					   now.after(ticket.getTime2()) ||
					   auth.getTimeRequest().getTime() < (now.getTime() - 2000)) {
						throw new RuntimeException("Ticket expired");
					}
					
					
					RequestTime requestTimeIntermediate = new RequestTime();
					requestTimeIntermediate.setTimeRequest(auth.getTimeRequest());
					CipheredView requestTimeCipher = requestTimeIntermediate.cipher(ticket.getKeyXY());
					
					RequestTime requestTimeToSend = new RequestTime(requestTimeCipher, ticket.getKeyXY());
					
					requestTimeSend = requestTimeToSend;
					keyKcs = ticket.getKeyXY();
					smc.put("kcs", keyKcs);	
					
					//remove headers
					header.removeChild(nodeTicket);
					header.removeChild(nodeAuth);
				
					}	
				} catch (SOAPException e) {
					throw new RuntimeException("Problem getting soap.");
				} catch (KerbyException e) {
					throw new RuntimeException("Problem running Kerberos protocol.");
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					throw new RuntimeException("Couldnt get key.");
				}
			
		}
		
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getPassword(){
		try {
			Properties prop = new Properties();
			prop.load(KerberosServerHandler.class.getResourceAsStream(TEST_PROP_FILE));
			return prop.getProperty("binas.password");
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
		}
		
		return null;
	}
	
	private String getServerEmail(){
		try {
			Properties prop = new Properties();
			prop.load(KerberosServerHandler.class.getResourceAsStream(TEST_PROP_FILE));
			return prop.getProperty("binas.server");
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
		}
		
		return null;
	}
	
	private String toBase64(byte[] data){
		byte[] encoded = Base64.getEncoder().encode(data);
		return new String(encoded);
	}
	
	private String toBase64(CipheredView view){
		byte[] encoded = Base64.getEncoder().encode(view.getData());
		return new String(encoded);
	}
	
	private CipheredView fromBase64(String data){
		CipheredView view = new CipheredView();
		
		byte[] decoded = Base64.getDecoder().decode(data);
		view.setData(decoded);
		return view;
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
	
	private Key getKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return SecurityHelper.generateKeyFromPassword(password);
	}

}
