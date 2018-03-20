package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class PingIT extends BaseIT {

	// tests
	// assertEquals(expected, actual);

	// public String ping(String x)

	@Test
	public void pingNullTest() {
		assertNotNull(client.testPing(null));
	}
	
	@Test
	public void pingBlankTest() {
		assertNotNull(client.testPing(""));
	}
	
	@Test
	public void pingEmptyTest() {
		assertNotNull(client.testPing("test"));
	}

}
