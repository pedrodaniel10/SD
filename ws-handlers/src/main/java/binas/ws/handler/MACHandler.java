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

import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

public class MACHandler implements SOAPHandler<SOAPMessageContext> {
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
				
				//get body to bytes
				DOMSource source = new DOMSource(smc.getMessage().getSOAPBody());
				StringWriter stringResult = new StringWriter();
				TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
				String message = stringResult.toString();
				byte[] plainBytes = message.getBytes();
				
				//digest
				MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
				
				messageDigest.update(plainBytes);
				byte[] digest = messageDigest.digest();
								
				//cipher digest with kcs
				Cipher cipher = SecurityHelper.initCipher((Key) smc.get("kcs"));
				byte[] cipherBytes = cipher.doFinal(digest);
				CipheredView digestCiphered = new CipheredView();
				digestCiphered.setData(cipherBytes);
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				//write to header
				SOAPHeader header = se.getHeader();
				
				if(header == null) {
					header = se.addHeader();
				}
				
				//add digest to header
				javax.xml.soap.Name nameDigest = se.createName("digest", "security", XML_TARGET_NAMESPACE);
				SOAPHeaderElement elementDigest = header.addHeaderElement(nameDigest);
				
				String stringDigest = toBase64(digestCiphered);
				elementDigest.addTextNode(stringDigest);		

			} 
			else {
				System.out.println("MACHandler: Reading header from INbound SOAP message...");		
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				//write to header
				SOAPHeader header = se.getHeader();
				
				if(header == null) {
					return true; //do nothing
				}
				
				//get digest from header
				SOAPElement digestElement = getSoapElement(se, header, "digest");
				
				if(digestElement == null){
					return true; // do nothing
				}
				else{
					String digestString = digestElement.getTextContent();
					CipheredView digestHeaderCiphered = fromBase64(digestString);
					
					//decipher
					if(smc.get("kcs") == null){
						return true; //do nothing
					}
					Cipher decipher = SecurityHelper.initDecipher((Key) smc.get("kcs"));
					byte[] digestToCompare = decipher.doFinal(digestHeaderCiphered.getData());
										
					//get body to bytes
					DOMSource source = new DOMSource(smc.getMessage().getSOAPBody());
					StringWriter stringResult = new StringWriter();
					TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
					String message = stringResult.toString();
					byte[] plainBytes = message.getBytes();
					
					//calculate digest
					MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
					
					messageDigest.update(plainBytes);
					byte[] digest = messageDigest.digest();
					
					//compare digests
					if(!Arrays.equals(digestToCompare,digest)){
						System.out.println(toBase64(digestToCompare));
						System.out.println(toBase64(digest));
						throw new RuntimeException("MAC doesnt match.");
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

}
