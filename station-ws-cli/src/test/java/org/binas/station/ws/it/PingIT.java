package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class PingIT extends BaseIT {

	@Test
	public void sucess() {
		assertNotNull(client.testPing("test"));
	}
	
	@Test
	public void pingNullTest() {
		assertNotNull(client.testPing(null));
	}
	
	@Test
	public void pingEmptyTest() {
		assertNotNull(client.testPing(""));
	}
	
	@Test
	public void pingBlankTest() {
		assertNotNull(client.testPing("   "));
	}

}
