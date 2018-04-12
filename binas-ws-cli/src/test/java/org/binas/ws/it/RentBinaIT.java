package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
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
	private final String EMAIL = "test@binas";
	
	@Before
	public void begin() throws Exception {
		oneTimeSetup();
	}
	
	@Test
	public void success() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception {

		client.activateUser(EMAIL);
		client.testInitStation("A47_Station1", 22, 7, 10, 2);
		client.rentBina(STATION_ID, EMAIL);
		assertEquals(client.getCredit(EMAIL), 9);
		StationView sv = client.getInfoStation(STATION_ID);
		assertEquals(sv.getFreeDocks(), 1);
		assertEquals(sv.getTotalGets(), 1);
		assertEquals(sv.getAvailableBinas(), 9);
		assertEquals(sv.getTotalReturns(), 0);
		
	}
	
	@Test 
	public void noCredit() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, UserNotExists_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception {
		client.activateUser("teste@binas");
		client.testInitStation("A47_Station1", 22, 7, 1, 2);
		int i = 0;
		int valor = client.getCredit("teste@binas");
		while (i < valor) {
			client.rentBina("A47_Station1", "teste@binas");	
		}
		client.rentBina("A47_Station1", "teste@binas");	
	}
	
	@Test
	public void userNotExists(){
		try{
			client.rentBina("A47_Station1", "teste@binas");	
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


