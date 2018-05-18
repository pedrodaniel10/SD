package binas.ws.handler;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;

public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext> {
	private static final String XML_TARGET_NAMESPACE = "http://ws.binas.org/";
	private static final String TEST_PROP_FILE = "/client.properties";
	private static SecureRandom randomGenerator = new SecureRandom();
	private static final int VALID_DURATION = 60;
	private Date tReq = null;
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
		System.out.println("KerberosClientHandler: Handling message.");
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

			if (outboundElement.booleanValue()) {
				System.out.println("KerberosClientHandler: Writing header to OUTbound SOAP message...");
				try{	
					KerbyClient client = new KerbyClient(getWSURL());
					long nounce = randomGenerator.nextLong();
										
					String email = getEmail();
					//get ticket from kerby
					SessionKeyAndTicketView result = client.requestTicket(email, getServerEmail(), nounce, VALID_DURATION);
				
					
					CipheredView cipheredSessionKey = result.getSessionKey();
					CipheredView cipheredTicket = result.getTicket();
					
					//get password to generate kc
					String password = getPassword();
					
					
					
					Key clientKey = null;
					if(password == null){
						throw new RuntimeException("No password found for user");
					}
					else {
						clientKey = getKey(password);
					}
					
					
					SessionKey sessionKey = new SessionKey(cipheredSessionKey, clientKey);
					
					//check nounce
					if(sessionKey.getNounce() != nounce){
						throw new RuntimeException("Nounce is not the same");
					}
											
					keyKcs = sessionKey.getKeyXY();
					smc.put("kcs", keyKcs);	
					
					//create auth
					this.tReq = new Date();
					Auth authIntermediate = new Auth(email, this.tReq);
					
					CipheredView authView = authIntermediate.cipher(keyKcs);	
					
					// get SOAP envelope
					SOAPMessage msg = smc.getMessage();
					SOAPPart sp = msg.getSOAPPart();
					SOAPEnvelope se = sp.getEnvelope();
					//write to header
					SOAPHeader header = se.getHeader();
					
					if(header == null) {
						header = se.addHeader();
					}
					
					//add auth
					javax.xml.soap.Name nameAuth = se.createName("auth", "security", XML_TARGET_NAMESPACE);
					SOAPHeaderElement elementAuth = header.addHeaderElement(nameAuth);
					
					String stringAuth = toBase64(authView);
					elementAuth.addTextNode(stringAuth);
					
					//add ticket
					javax.xml.soap.Name ticketAuth = se.createName("ticket", "security", XML_TARGET_NAMESPACE);
					SOAPHeaderElement elementTicket = header.addHeaderElement(ticketAuth);
					
					String stringTicket = toBase64(result.getTicket());
					elementTicket.addTextNode(stringTicket);
														
				}
				catch(BadTicketRequest_Exception e){
					throw new RuntimeException("Bad ticket Exception");
				} catch (KerbyClientException e) {
					throw new RuntimeException("Couldnt create kerby client.");
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					throw new RuntimeException("Couldnt get key.");
				}  catch (SOAPException e) {
					throw new RuntimeException("Problem getting soap.");
				} catch (KerbyException e) {
					throw new RuntimeException("Problem running Kerberos protocol.");
				}


			} 
			else {
				System.out.println("KerberosClientHandler: Reading header from INbound SOAP message...");
				try{
					// get SOAP envelope
					SOAPMessage msg = smc.getMessage();
					SOAPPart sp = msg.getSOAPPart();
					SOAPEnvelope se = sp.getEnvelope();
					//write to header
					SOAPHeader header = se.getHeader();
					
					if(header == null) {
						throw new RuntimeException("No header to get treq.");
					}
					
					//get REQTIME
					SOAPElement reqTimeElement = getSoapElement(se, header, "requestTime");
					
					if(reqTimeElement == null){
						throw new RuntimeException("No reqtime.");
					}
					else{
						String treqString = reqTimeElement.getTextContent();
						CipheredView treq = fromBase64(treqString);
						
						//check treq
						RequestTime reqReceived = new RequestTime(treq, keyKcs);
						
						if(!reqReceived.getTimeRequest().equals(this.tReq)){
							System.out.println("Treq received:" + reqReceived.getTimeRequest());
							System.out.println("Treq sent:" + this.tReq);
							throw new RuntimeException("TREQ received doesnt match with TREQ sent.");
						}
						
						//delete header
						header.removeChild(reqTimeElement);
					}
				
				} catch (SOAPException e) {
					throw new RuntimeException("Problem getting soap.");
				} catch (KerbyException e) {
					throw new RuntimeException("Problem running Kerberos protocol.");
				} 
			}
		
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getEmail(){
		try {
			Properties prop = new Properties();
			prop.load(KerberosClientHandler.class.getResourceAsStream(TEST_PROP_FILE));
			return prop.getProperty("client.email");
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
		}
		
		return null;
	}
	
	private String getPassword(){
		try {
			Properties prop = new Properties();
			prop.load(KerberosClientHandler.class.getResourceAsStream(TEST_PROP_FILE));
			return prop.getProperty("client.password");
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
		}
		
		return null;
	}
	
	private String getServerEmail(){
		try {
			Properties prop = new Properties();
			prop.load(KerberosClientHandler.class.getResourceAsStream(TEST_PROP_FILE));
			return prop.getProperty("binas.server");
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
		}
		
		return null;
	}
	
	private String getWSURL(){
		try {
			Properties prop = new Properties();
			prop.load(KerberosClientHandler.class.getResourceAsStream(TEST_PROP_FILE));
			return prop.getProperty("ws.url");
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
