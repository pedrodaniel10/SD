package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Test;

public class ReturnBinaIT extends BaseIT {

	@Test
	public void sucess() throws BadInit_Exception, NoBinaAvail_Exception, NoSlotAvail_Exception{
		client.testInit(3, 3, 30, 0);
		client.getBina();
		
		client.returnBina();
		
		StationView stationView = client.getInfo();
		
		assertEquals(1, stationView.getTotalReturns());
		assertEquals(0, stationView.getFreeDocks());
		assertEquals(30, stationView.getAvailableBinas());
	}
	
	@Test(expected = NoSlotAvail_Exception.class)
	public void noAvailableSlots() throws BadInit_Exception, NoSlotAvail_Exception {
		client.testInit(3, 3, 30, 0);
		client.returnBina();
	}
	
	@Test(expected = NoSlotAvail_Exception.class)
	public void zeroCapacity() throws BadInit_Exception, NoSlotAvail_Exception {
		client.testInit(3, 3, 0, 0);
		client.returnBina();
	}
	
	@Test
	public void manyBikes() throws BadInit_Exception, NoBinaAvail_Exception, NoSlotAvail_Exception{
		client.testInit(3, 3, 30, 0);
		for(int i = 0; i < 30; i++){
			client.getBina();
		}
		
		for(int i = 0; i < 30; i++){
			client.returnBina();
		}
		
		StationView stationView = client.getInfo();
		
		assertEquals(30, stationView.getTotalReturns());
		assertEquals(0, stationView.getFreeDocks());
		assertEquals(30, stationView.getAvailableBinas());
	}
	
	@Test(expected = NoSlotAvail_Exception.class)
	public void returnFailed() throws BadInit_Exception, NoBinaAvail_Exception, NoSlotAvail_Exception{
		client.testInit(3, 3, 30, 0);
		for(int i = 0; i < 30; i++){
			client.getBina();
		}
		
		for(int i = 0; i < 30; i++){
			client.returnBina();
		}
		
		client.returnBina();
	}
	
	@Test
	public void manyBikesReturned() throws BadInit_Exception, NoBinaAvail_Exception, NoSlotAvail_Exception{
		client.testInit(3, 3, 30, 0);
		for(int i = 0; i < 30; i++){
			client.getBina();
		}
		
		for(int i = 0; i < 30; i++){
			client.returnBina();
		}
		
		for(int i = 0; i < 30; i++){
			client.getBina();
		}
		
		for(int i = 0; i < 30; i++){
			client.returnBina();
		}
		
		StationView stationView = client.getInfo();
		
		assertEquals(60, stationView.getTotalReturns());
		assertEquals(0, stationView.getFreeDocks());
		assertEquals(30, stationView.getAvailableBinas());
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
}
