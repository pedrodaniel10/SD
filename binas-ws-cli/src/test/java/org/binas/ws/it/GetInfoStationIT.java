package org.binas.ws.it;

import static org.junit.Assert.assertEquals;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;
import org.junit.Test;


/**
 * Test suite
 */
public class GetInfoStationIT extends BaseIT {

	@Test
	public void sucess() throws BadInit_Exception, InvalidStation_Exception{
		client.testInitStation("A47_Station1", 22, 7, 6, 2);
		client.testInitStation("A47_Station2", 80, 20, 12, 1);
		client.testInitStation("A47_Station3", 50, 50, 20, 0);
		
		StationView station1 = client.getInfoStation("A47_Station1");
		StationView station2 = client.getInfoStation("A47_Station2");
		StationView station3 = client.getInfoStation("A47_Station3");
		
		assertEquals(22, (int)station1.getCoordinate().getX());
		assertEquals(7, (int)station1.getCoordinate().getY());
		assertEquals(6, station1.getCapacity());
		assertEquals(6, station1.getAvailableBinas());
		
		assertEquals(80, (int)station2.getCoordinate().getX());
		assertEquals(20, (int)station2.getCoordinate().getY());
		assertEquals(12, station2.getCapacity());
		assertEquals(12, station2.getAvailableBinas());
		
		assertEquals(50, (int)station3.getCoordinate().getX());
		assertEquals(50, (int)station3.getCoordinate().getY());
		assertEquals(20, station3.getCapacity());
		assertEquals(20, station3.getAvailableBinas());
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void nullStation() throws InvalidStation_Exception{
		client.getInfoStation(null);
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void emptyStation() throws InvalidStation_Exception{
		client.getInfoStation("");
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void blankStation() throws InvalidStation_Exception{
		client.getInfoStation("   ");
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void wrongStation() throws InvalidStation_Exception{
		client.getInfoStation("WrongStation");
	}
	
}
