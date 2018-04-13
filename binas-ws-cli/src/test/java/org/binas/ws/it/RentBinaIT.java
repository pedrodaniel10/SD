package org.binas.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.junit.After;
import org.junit.Test;



/**
 * Test suite for rentBina, it is assumed that 3 station-ws and a binas-ws were initiated sucessefully.
 */

public class RentBinaIT extends BaseIT {
	
	private final String EMAIL = "test@binas";
	
	@Test
	public void success() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception, UserNotExists_Exception, EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception {

		client.activateUser(EMAIL);
		client.testInitStation("A47_Station1", 22, 7, 10, 2);
		client.rentBina("A47_Station1", EMAIL);
		assertEquals(client.getCredit(EMAIL), 9);
		StationView sv = client.getInfoStation("A47_Station1");
		assertEquals(1, sv.getFreeDocks());
		assertEquals(1, sv.getTotalGets());
		assertEquals(9, sv.getAvailableBinas());
		assertEquals(0, sv.getTotalReturns());
		
	}

	@Test 
	public void creditEqualsOneSucess() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, 
		UserNotExists_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception{
		client.testInitStation("A47_Station1", 22, 7, 10, 2);
		client.testInit(1);
		
		client.activateUser("teste@binas");
		client.rentBina("A47_Station1", "teste@binas");
		
		StationView sv = client.getInfoStation("A47_Station1");
		assertEquals(1, sv.getFreeDocks());
		assertEquals(1, sv.getTotalGets());
		assertEquals(9, sv.getAvailableBinas());
		assertEquals(0, sv.getTotalReturns());
	}
	
	@Test 
	public void noCredit() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, 
		UserNotExists_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception{
		client.testInitStation("A47_Station1", 22, 7, 1, 2);
		client.testInit(0);
		
		client.activateUser("teste@binas");
		try{
			client.rentBina("A47_Station1", "teste@binas");
			fail();
		}
		catch(NoCredit_Exception e){
			StationView station = client.getInfoStation("A47_Station1");
			assertEquals(0, station.getFreeDocks());
			assertEquals(0, station.getTotalGets());
			assertEquals(1, station.getAvailableBinas());
		}
	}

	@Test
	public void alreadyHasBina() throws EmailExists_Exception, InvalidEmail_Exception, 
		InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
		BadInit_Exception {
		client.activateUser("teste@binas");
		client.testInitStation("A47_Station1", 22, 7, 10, 2);
		client.testInitStation("A47_Station2", 80, 20, 10, 2);

		try {
			client.rentBina("A47_Station1", "teste@binas");
			client.rentBina("A47_Station2", "teste@binas");
		} 
		catch (AlreadyHasBina_Exception e) {
			StationView station1 = client.getInfoStation("A47_Station1");
			assertEquals(1, station1.getFreeDocks());
			assertEquals(1, station1.getTotalGets());
			assertEquals(9, station1.getAvailableBinas());
			
			StationView station2 = client.getInfoStation("A47_Station2");
			assertEquals(0, station2.getFreeDocks());
			assertEquals(0, station2.getTotalGets());
			assertEquals(10, station2.getAvailableBinas());
		}
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void invalidStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, 
		InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.activateUser("teste@binas");
		client.rentBina("CXX_Station1", "teste@binas");
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void nullStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, 
		InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.activateUser("teste@binas");
		client.rentBina(null, "teste@binas");
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void emptyStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, 
		InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.activateUser("teste@binas");
		client.rentBina("", "teste@binas");
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void blankStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, 
		InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.activateUser("teste@binas");
		client.rentBina(" ", "teste@binas");
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void noBinaAvailStation1() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, 
		AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, 
		UserNotExists_Exception{
		client.activateUser("teste@binas");
		client.testInitStation("A47_Station1", 22, 7, 0, 2);
		client.rentBina("A47_Station1", "teste@binas");	
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void emailNull() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception, UserNotExists_Exception{
		client.rentBina("A47_Station1", null);	
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void emailEmpty() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception, UserNotExists_Exception{
		client.rentBina("A47_Station1", "");	
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void emailBlank() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception, UserNotExists_Exception{
		client.rentBina("A47_Station1", "  ");	
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception, UserNotExists_Exception{
		client.rentBina("A47_Station1", "teste@binas");	
	}
		
	@After
	public void tearDown(){
		client.testClear();
	}
}


