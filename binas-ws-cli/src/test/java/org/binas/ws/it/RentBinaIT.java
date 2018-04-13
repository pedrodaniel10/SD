package org.binas.ws.it;

import static org.junit.Assert.assertEquals;

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
import org.junit.After;
import org.junit.Test;



/**
 * Test suite for rentBina, it is assumed that 3 station-ws and a binas-ws were initiated sucessefully.
 */

public class RentBinaIT extends BaseIT {
	
	private final String EMAIL = "test@binas";
	
	@Test
	public void success() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception {

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
	public void noCredit() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, 
		UserNotExists_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception {
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
	
	@Test (expected = InvalidStation_Exception.class)
	public void invalidStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.activateUser("teste@binas");
		client.rentBina("CXX_Station1", "teste@binas");
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void nullStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.activateUser("teste@binas");
		client.rentBina(null, "teste@binas");
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void emptyStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.activateUser("teste@binas");
		client.rentBina("", "teste@binas");
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void blankStation() throws EmailExists_Exception, InvalidEmail_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
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
	
	@Test (expected = NoBinaRented_Exception.class)
	public void noBinaRented() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception {
		client.activateUser("teste@binas");
		client.activateUser("teste2@binas");
		client.testInitStation("A47_Station1", 22, 7, 4, 2);
		client.rentBina("A47_Station1", "teste@binas");
		client.returnBina("A47_Station1", "teste2@binas");
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception{
		client.rentBina("A47_Station1", "teste@binas");	
	}
		
	@After
	public void tearDown(){
		client.testClear();
	}
}


