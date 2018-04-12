package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserNotExists_Exception;
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
	public void sucessOtherDefaultCredit() throws UserNotExists_Exception, 
		EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception{
		client.testInit(20);
		UserView userView = client.activateUser("test@binas");
		
		assertEquals("test@binas", userView.getEmail());
		assertEquals(20, userView.getCredit().intValue());
		assertFalse(userView.isHasBina());
	}
	
	@Test
	public void sucess3() throws EmailExists_Exception, InvalidEmail_Exception{
		UserView userView = client.activateUser("test@binas.binas.binas");
		
		assertEquals("test@binas.binas.binas", userView.getEmail());
		assertEquals(10, userView.getCredit().intValue());
		assertFalse(userView.isHasBina());
	}
	
	@Test(expected = EmailExists_Exception.class)
	public void uniqueEmail() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("test@binas");
		client.activateUser("test@binas");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void nullEmail() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser(null);
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void blankEmail() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("   ");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailNoAt() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("userdomain");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailNoUserAndNoDomain() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("@");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailNoUser() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("@domain");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailNoDomain() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("user@");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailUserPartEmpty() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser(".user@domain");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailUserPartEmpty2() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("user.@domain");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailUserPartEmpty3() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("user..user@domain");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailDomainPartEmpty() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("user@.domain");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailDomainPartEmpty2() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("user@domain.");

	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emailDomainPartEmpty3() throws EmailExists_Exception, InvalidEmail_Exception{
		client.activateUser("user@domain..domain");
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
	
}


