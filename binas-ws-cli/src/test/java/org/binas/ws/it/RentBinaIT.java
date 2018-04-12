package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Test;



/**
 * Test suite for rentBina, it is assumed that 3 station-ws and a binas-ws were initiated sucessefully.
 */

public class RentBinaIT extends BaseIT {
	
	private final int NUMBER_STATIONS = 3;
	private final String STATION_ID = "A47_Binas";
	private final String EMAIL = "caldeira.a.pedro@gmail.com";
	
	@Test
	public void success() {
		User user = new User(EMAIL);
		client.rentBina(STATION_ID, EMAIL);
	}
}


