package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.UserNotExists_Exception;
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
	
	@Test
	public void userNotExists(){
		try{
			client.rentBina("A47_Station1", "teste@binas");	
			fail();
		}
		catch(UserNotExists_Exception e){
//			It should go here
		}
		catch(Exception e){
//			any other exception the test should fail
			fail();
		}
	}
	
	@Test 
	public void noBinaAvailStation1(){
		try {
//			create user
			client.activateUser("teste@binas");
//			define station with 0 capacity
			client.testInitStation("A47_Station1", 22, 7, 0, 2);
			
			client.rentBina("A47_Station1", "teste@binas");
			fail();
		} 
		catch(NoBinaAvail_Exception e){
			
		}
		catch (Exception e) {
			fail();
		}
		
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
}


