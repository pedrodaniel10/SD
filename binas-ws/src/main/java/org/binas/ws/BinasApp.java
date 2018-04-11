package org.binas.ws;

import org.binas.domain.BinasManager;

public class BinasApp {

	public static void main(String[] args) throws Exception {
		String wsName;
		String wsURL;
		String uddiURL;
		
		BinasManager binasM = BinasManager.getInstance();
		
		if (args.length == 3){
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			BinasManager.getInstance().setBinas(uddiURL, wsName, wsURL);
			System.out.println("{wsName = " + wsName + ", wsURL = " + wsURL + ", uddiURL = " + uddiURL + "}");
		}
		else{
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java Binas uddiURL wsName wsURL");
			return;
		}
	
		

		try {
			binasM.start();
			
			BinasPortImpl implementation = binasM.getPort();
			System.out.println("Invoke ping()...");
			System.out.println(implementation.testPing("client"));
			
			binasM.awaitConnections();
		} finally {
			binasM.stop();
		}

	}
		
}

