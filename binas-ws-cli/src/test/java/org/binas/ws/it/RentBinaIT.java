package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * Test suite for rentBina, it is assumed that 3 station-ws and a binas-ws were initiated sucessefully.
 */

public class RentBinaIT extends BaseIT {
	
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
	
	@Test (expected = NoCredit_Exception.class)
	public void noCredit() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, UserNotExists_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception {
		client.activateUser("teste@binas");
		client.testInitStation("A47_Station1", 22, 7, 1, 2);
		client.testInit(0);
		client.rentBina("A47_Station1", "teste@binas");
	}

	@Test (expected = AlreadyHasBina_Exception.class)
	public void alreadyHasBina() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, BadInit_Exception {
		client.activateUser("teste@binas");
		client.testInitStation("A47_Station1", 22, 7, 1, 2);
		client.rentBina("A47_Station1", "teste@binas");
		client.rentBina("A47_Station1", "teste@binas");
	}
	
	@Test (expected = EmailExists_Exception.class)
	public void emailAlreadyExists() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("teste@binas");
		client.activateUser("teste@binas");
	}
	
	@Test (expected = FullStation_Exception.class)
	public void fullStationTest() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception {
		client.activateUser("teste@binas");
		client.testInitStation("A47_Station1", 22, 7, 1, 2);
		client.testInitStation("A47_Station2", 7, 7, 1, 2);
		client.rentBina("A47_Station1", "teste@binas");
		client.returnBina("A47_Station2", "teste@binas");
	}
	
	@Test (expected = InvalidEmail_Exception.class)
	public void invalidEmail() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("testebinas");
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void invalidStation() throws BadInit_Exception {
		client.testInitStation("CXX_Station1", 22, 7, 1, 2);
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
	
	
	@After
	public void tearDown(){
		client.testClear();
	}
}


