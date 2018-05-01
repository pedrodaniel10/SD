package org.binas.ws;

public class BinasApp {

	public static void main(String[] args) throws Exception {
		String wsName;
		String wsURL;
		String uddiURL;
		
		BinasEndpointManager endpoint;
		
		if (args.length == 3){
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);
			System.out.println("{wsName = " + wsName + ", wsURL = " + wsURL + ", uddiURL = " + uddiURL + "}");
		}
		else{
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java Binas uddiURL wsName wsURL");
			return;
		}
	
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}

	}
		
}

