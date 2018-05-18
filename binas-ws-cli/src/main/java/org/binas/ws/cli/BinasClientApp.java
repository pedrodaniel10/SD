package org.binas.ws.cli;

public class BinasClientApp {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length == 0) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + BinasClientApp.class.getName()
                    + " wsURL OR uddiURL wsName");
            return;
        }
        String uddiURL = null;
        String wsName = null;
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
            System.out.println("{wsURL = " + wsURL + "}");
        } else if (args.length >= 2) {
            uddiURL = args[0];
            wsName = args[1];
            System.out.println("{uddiURL = " + uddiURL + ", wsName = " + wsName + "}");
        }

		System.out.println(BinasClientApp.class.getSimpleName() + " running");

        // Create client
        BinasClient client = null;

        if (wsURL != null) {
            System.out.printf("Creating client for server at %s%n", wsURL);
            client = new BinasClient(wsURL);
        } else if (uddiURL != null) {
            System.out.printf("Creating client using UDDI at %s for server with name %s%n",
                uddiURL, wsName);
            client = new BinasClient(uddiURL, wsName);
        }

        // the following remote invocations are just basic examples
        // the actual tests are made using JUnit

		 System.out.println("Invoke rentBina(alice@A47.binas.org)...");
		 client.rentBina("A47_Station1", "alice@A47.binas.org");
		 System.out.println("Invoke returnBina(alice@A47.binas.org)...");
		 client.returnBina("A47_Station1", "alice@A47.binas.org");
		 System.out.println("Invoke getCredit(alice@A47.binas.org)...");
		 client.getCredit("alice@A47.binas.org");
        
	 }
}

