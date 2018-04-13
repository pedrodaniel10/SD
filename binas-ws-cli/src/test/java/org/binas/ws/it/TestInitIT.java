package org.binas.ws.it;

import org.binas.ws.BadInit_Exception;
import org.junit.Test;

public class TestInitIT extends BaseIT {
	
	@Test
	public void onePoint() throws BadInit_Exception{
		client.testInit(1);
	}
	
	@Test
	public void zeroPoints() throws BadInit_Exception{
		client.testInit(0);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void minusOnePoints() throws BadInit_Exception{
		client.testInit(-1);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void negativePoints() throws BadInit_Exception{
		client.testInit(-5);
	}
}
