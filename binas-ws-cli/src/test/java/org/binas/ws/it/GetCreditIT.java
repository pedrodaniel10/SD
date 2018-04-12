package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.junit.After;
import org.junit.Test;

public class GetCreditIT extends BaseIT {
	private static final String EMAIL = "teste@binas";
	private static final String EMAIL2 = "teste.teste@binas";

	@Test
	public void sucess() throws UserNotExists_Exception, EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser(EMAIL);
		assertEquals(10, client.getCredit(EMAIL));
	}
	
	@Test
	public void sucessOtherDefaultCredit() throws UserNotExists_Exception, 
		EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception{
		client.testInit(20);
		client.activateUser(EMAIL);
		
		assertEquals(20, client.getCredit(EMAIL));
	}
	
	@Test
	public void sucessOtherDefaultBetweenCredit() throws UserNotExists_Exception, 
		EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception{
		client.activateUser(EMAIL);
		client.testInit(20);
		client.activateUser(EMAIL2);
		
		assertEquals(10, client.getCredit(EMAIL));
		assertEquals(20, client.getCredit(EMAIL2));
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void nullEmail() throws UserNotExists_Exception{
		client.getCredit(null);
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void emptyEmail() throws UserNotExists_Exception{
		client.getCredit("");
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void blankEmail() throws UserNotExists_Exception{
		client.getCredit("   ");
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void wrongEmail() throws UserNotExists_Exception{
		client.getCredit("testebinas");
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExists() throws UserNotExists_Exception{
		client.getCredit(EMAIL);
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
}
