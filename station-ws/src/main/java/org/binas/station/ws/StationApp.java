package org.binas.station.ws;

import org.binas.station.domain.Station;

/**
 * The application is where the service starts running. The program arguments
 * are processed here. Other configurations can also be done here.
 */
public class StationApp {

	public static void main(String[] args) throws Exception {
		String wsName;
		String wsURL;
		String uddiURL;
		StationEndpointManager endpoint;
		if(args.length == 2){
			wsName = args[0];
			wsURL = args[1];
			endpoint = new StationEndpointManager(wsName, wsURL);
			System.out.println("{wsName = " + wsName + ", wsURL = " + wsURL + "}");
		}
		else if (args.length == 3){
			wsName = args[0];
			wsURL = args[1];
			uddiURL = args[2];
			endpoint = new StationEndpointManager(uddiURL, wsName, wsURL);
			System.out.println("{wsName = " + wsName + ", wsURL = " + wsURL + ", uddiURL = " + uddiURL + "}");
		}
		else{
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + StationApp.class.getName() + "wsName wsURL OR wsName wsURL uddiURL");
			return;
		}

		
		Station.getInstance().setId(wsName);

		System.out.println(StationApp.class.getSimpleName() + " running");

			
		// TODO start Web Service

		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}

	}

}