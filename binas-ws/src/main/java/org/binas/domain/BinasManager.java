package org.binas.domain;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import org.binas.ws.BinasPortImpl;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class BinasManager {
	/** UDDI naming server location */
	private static String uddiURL = null;
	
	/** Web Service name */
	private static String wsName = null;
	
	/** Binas url*/
	private static String wsURL;
	
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
		if(uddiNaming == null){
			this.uddiNaming = new UDDINaming(uddiURL);
		}
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
	
	// Singleton -------------------------------------------------------------
	private BinasManager() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public synchronized void setBinas(String UDDIurl, String wsName, String wsURL) {
		this.setUDDIUrl(UDDIurl);
		this.setWsName(wsName);
		this.setWsURL(wsURL);
	}
	
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

}
