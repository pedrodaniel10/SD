package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Test;

public class ActiveUserIT extends BaseIT {
	
	@Test
	public void sucess() throws EmailExists_Exception, InvalidEmail_Exception{
		UserView userView = client.activateUser("test@binas");
		
		assertEquals("test@binas", userView.getEmail());
		assertEquals(10, userView.getCredit().intValue());
		assertFalse(userView.isHasBina());
	}
	
	@Test
	public void sucess2() throws EmailExists_Exception, InvalidEmail_Exception{
		UserView userView = client.activateUser("test.test.test@binas");
		
		assertEquals("test.test.test@binas", userView.getEmail());
		assertEquals(10, userView.getCredit().intValue());
		assertFalse(userView.isHasBina());
	}
	
	@Test
	public void sucess3() throws EmailExists_Exception, InvalidEmail_Exception{
		UserView userView = client.activateUser("test@binas.binas.binas");
		
		assertEquals("test@binas.binas.binas", userView.getEmail());
		assertEquals(10, userView.getCredit().intValue());
		assertFalse(userView.isHasBina());
	}
	
	@Test
	public void uniqueEmail(){
		try {
			client.activateUser("test@binas");
			client.activateUser("test@binas");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
			fail();
		} 
		catch (EmailExists_Exception e) {
//			supposed to go here	
		}
	}
	
	@Test
	public void nullEmail(){
		try {
			client.activateUser(null);
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void blankEmail(){
		try {
			client.activateUser("   ");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailNoAt(){
		try {
			client.activateUser("userdomain");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailNoUserAndNoDomain(){
		try {
			client.activateUser("@");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailNoUser(){
		try {
			client.activateUser("@domain");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailNoDomain(){
		try {
			client.activateUser("user@");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailUserPartEmpty(){
		try {
			client.activateUser(".user@domain");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailUserPartEmpty2(){
		try {
			client.activateUser("user.@domain");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailUserPartEmpty3(){
		try {
			client.activateUser("user..user@domain");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailDomainPartEmpty(){
		try {
			client.activateUser("user@.domain");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailDomainPartEmpty2(){
		try {
			client.activateUser("user@domain.");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test
	public void emailDomainPartEmpty3(){
		try {
			client.activateUser("user@domain..domain");
			fail();
		} 
		catch (InvalidEmail_Exception e) {
//			supposed to go here
		} 
		catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
	
}


