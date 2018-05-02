package org.binas.ws;

public class BinasApp {

	public static void main(String[] args) throws Exception {
		String wsName;
		String wsURL;
		String uddiURL;
		int replics;
		
		BinasEndpointManager endpoint;
		
		if (args.length == 4){
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			replics = Integer.parseInt(args[3]);
			if(replics < 3){
				System.out.println("Number of Replics should be greater or equals to 3.");
				return;
			}
			endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL, replics);
			System.out.println("{wsName = " + wsName + ", wsURL = " + wsURL + ", uddiURL = " + uddiURL + ", replics = " + replics + "}");
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

