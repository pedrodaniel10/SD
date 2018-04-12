package org.binas.ws.it;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Test suite for pingTest, it is assumed that 3 station-ws and a binas-ws were initiated sucessefully.
 */
public class PingIT extends BaseIT {

	private final int NUMBER_STATIONS = 3;
	private final String BINAS_NAME = "A47_Binas";
	private final String INPUT_MESSAGE = "Pedro";
	private final String DEFAULT_INPUT_MESSAGE = "friend";
	private final String HELLO_BINAS_FORMAT = "Hello %s from %s";
	private final String FOUND_X_STATIONS = "Founded %d stations.";
	private final String PINGING_FORMAT = "[Pinging Station = A47_Station%d][Answer] Hello %s from A47_Station%d";
	
	@Test
	public void sucess(){
		String result = client.testPing(INPUT_MESSAGE);
		
		assertNotNull(result);
		
		String[] lines = result.split("\n");
		assertEquals(NUMBER_STATIONS + 2, lines.length);
		assertEquals(String.format(HELLO_BINAS_FORMAT, INPUT_MESSAGE, BINAS_NAME), lines[0]);
		assertEquals(String.format(FOUND_X_STATIONS, NUMBER_STATIONS), lines[1]);
		for(int i = 1; i <=  NUMBER_STATIONS; i++)
			assertTrue(result.contains(String.format(PINGING_FORMAT, i, INPUT_MESSAGE, i)));
	}
	
	@Test
    public void pingNullTest() {
    	String result = client.testPing(null);
		
		assertNotNull(result);
		
		String[] lines = result.split("\n");
		assertEquals(NUMBER_STATIONS + 2, lines.length);
		assertEquals(String.format(HELLO_BINAS_FORMAT, DEFAULT_INPUT_MESSAGE, BINAS_NAME), lines[0]);
		assertEquals(String.format(FOUND_X_STATIONS, NUMBER_STATIONS), lines[1]);
		for(int i = 1; i <=  NUMBER_STATIONS; i++)
			assertTrue(result.contains(String.format(PINGING_FORMAT, i, DEFAULT_INPUT_MESSAGE, i)));
    }
	
    @Test
    public void pingEmptyTest() {
    	String result = client.testPing("");
		
		assertNotNull(result);
		
		String[] lines = result.split("\n");
		assertEquals(NUMBER_STATIONS + 2, lines.length);
		assertEquals(String.format(HELLO_BINAS_FORMAT, DEFAULT_INPUT_MESSAGE, BINAS_NAME), lines[0]);
		assertEquals(String.format(FOUND_X_STATIONS, NUMBER_STATIONS), lines[1]);
		for(int i = 1; i <=  NUMBER_STATIONS; i++)
			assertTrue(result.contains(String.format(PINGING_FORMAT, i, DEFAULT_INPUT_MESSAGE, i)));
    }
    
    @Test
    public void pingBlankTest() {
    	String result = client.testPing("     ");
		
		assertNotNull(result);
		
		String[] lines = result.split("\n");
		assertEquals(NUMBER_STATIONS + 2, lines.length);
		assertEquals(String.format(HELLO_BINAS_FORMAT, DEFAULT_INPUT_MESSAGE, BINAS_NAME), lines[0]);
		assertEquals(String.format(FOUND_X_STATIONS, NUMBER_STATIONS), lines[1]);
		for(int i = 1; i <=  NUMBER_STATIONS; i++)
			assertTrue(result.contains(String.format(PINGING_FORMAT, i, DEFAULT_INPUT_MESSAGE, i)));
    }

}
