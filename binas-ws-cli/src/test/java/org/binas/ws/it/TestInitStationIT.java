/**
 * 
 */
package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;
import org.junit.Test;

public class TestInitStationIT extends BaseIT {
	private static final String STATION_1 = "A47_Station1";
	private static final int X = 22;
	private static final int Y = 7;
	private static final int CAPACITY = 6;
	private static final int PRIZE = 2;
	
	@Test
	public void sucess() throws BadInit_Exception, InvalidStation_Exception{
		client.testInitStation(STATION_1, X, Y, CAPACITY, PRIZE);
		
		StationView station = client.getInfoStation(STATION_1);
		
		assertEquals(STATION_1, station.getId());
		assertEquals(X, (int)station.getCoordinate().getX());
		assertEquals(Y, (int)station.getCoordinate().getY());
		assertEquals(CAPACITY, station.getCapacity());
		assertEquals(CAPACITY, station.getAvailableBinas());
		assertEquals(0, station.getFreeDocks());
		assertEquals(0, station.getTotalGets());
		assertEquals(0, station.getTotalReturns());
	}
	
	@Test(expected = BadInit_Exception.class)
	public void nullStation() throws BadInit_Exception{
		client.testInitStation(null, X, Y, CAPACITY, PRIZE);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void emptyStation() throws BadInit_Exception{
		client.testInitStation("", X, Y, CAPACITY, PRIZE);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void blankStation() throws BadInit_Exception{
		client.testInitStation("  ", X, Y, CAPACITY, PRIZE);
	}
	
	@Test
	public void oneCapacity() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, 1, PRIZE);
	}
	
	@Test
	public void zeroCapacity() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, 0, PRIZE);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void minusOneCapacity() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, -1, PRIZE);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void negativeCapacity() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, -5, PRIZE);
	}
	
	@Test
	public void onePrize() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, CAPACITY, 1);
	}
	
	@Test
	public void zeroPrize() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, CAPACITY, 0);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void minusOnePrize() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, CAPACITY, -1);
	}
	
	@Test(expected = BadInit_Exception.class)
	public void negativePrize() throws BadInit_Exception{
		client.testInitStation(STATION_1, X, Y, CAPACITY, -5);
	}
}
