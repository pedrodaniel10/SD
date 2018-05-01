package org.binas.ws;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import org.binas.domain.BinasManager;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

/** The endpoint manager starts and registers the service. */
public class BinasEndpointManager {
	/** UDDI naming server location */
	private String uddiURL = null;
	
	/** Web Service name */
	private String wsName = null;
	
	/** Binas URL*/
	private String wsURL;
	
	/** Web Service end point */
	private Endpoint endpoint = null;
	
	/** Port implementation */
	private BinasPortImpl portImpl = new BinasPortImpl(this);
	
	/** Obtain Port implementation */
	public BinasPortImpl getPort() {
		return portImpl;
	}

	 /** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming = null;
	
	/** Get UDDI Naming instance for contacting UDDI server 
	 * @throws UDDINamingException */
	public synchronized UDDINaming getUddiNaming() throws UDDINamingException {
		this.uddiNaming = new UDDINaming(this.uddiURL);

		return uddiNaming;
	}
	 
	 /** output option */
	private boolean verbose = true;
	
	public boolean isVerbose() {
		return verbose;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	/** constructor with provided UDDI location, WS name, and WS URL */
	public BinasEndpointManager(String uddiURL, String wsName, String wsURL) {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		this.wsURL = wsURL;
		BinasManager.getInstance().setUDDIUrl(uddiURL);
	}
	
	// Getters and Setters --------------------------------------------------
	public String getUDDIUrl() {
		return uddiURL;
	}

	private void setUDDIUrl(String uDDIUrl) {
		this.uddiURL = uDDIUrl;
	}

	public String getWsName() {
		return wsName;
	}

	private void setWsName(String wsName) {
		this.wsName = wsName;
	}

	public String getWsURL() {
		return wsURL;
	}

	private void setWsURL(String wsURL) {
		this.wsURL = wsURL;
	}
	
	public void start() throws Exception {
		try {
			// publish end point
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		publishToUDDI();
	}

	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
		unpublishFromUDDI();
	}

	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				// stop end point
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
	}
	
	private synchronized void publishToUDDI() throws Exception {
		// publish to UDDI
		System.out.printf("Publishing '%s' to UDDI at %s%n", this.wsName, this.uddiURL);
		this.uddiNaming = new UDDINaming(this.uddiURL);
		this.uddiNaming.rebind(this.wsName, this.wsURL);
		
	}
	
	private synchronized void unpublishFromUDDI() {
		if (this.uddiNaming != null) {
			// delete from UDDI
			try {
				this.uddiNaming.unbind(this.wsName);
			} catch (UDDINamingException e) {
				System.out.printf("Caught exception when unbinding UDDINaming: %s%n", e);
			}
			System.out.printf("Deleted '%s' from UDDI%n", this.wsName);
		}
		
	}
}
