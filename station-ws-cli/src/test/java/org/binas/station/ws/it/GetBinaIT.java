package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Test;

public class GetBinaIT extends BaseIT {

	@Test
	public void sucess() throws NoBinaAvail_Exception, BadInit_Exception{
		client.testInit(3, 3, 30, 0);
		client.getBina();
		
		StationView stationView = client.getInfo();
		
		assertEquals(1, stationView.getTotalGets());
		assertEquals(1, stationView.getFreeDocks());
		assertEquals(29, stationView.getAvailableBinas());
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void noBikesAvailable() throws NoBinaAvail_Exception, BadInit_Exception{
		client.testInit(3, 3, 0, 0);
		client.getBina();
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
		
		for(int i = 0; i < 30; i++){
			client.getBina();
		}
		
		StationView stationView = client.getInfo();
		
		assertEquals(60, stationView.getTotalGets());
		assertEquals(30, stationView.getFreeDocks());
		assertEquals(0, stationView.getAvailableBinas());
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void moreGetsThanAvailableBinas() throws BadInit_Exception, NoBinaAvail_Exception, NoSlotAvail_Exception{
		client.testInit(3, 3, 30, 0);
		for(int i = 0; i < 30; i++){
			client.getBina();
		}
		
		client.getBina();
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
	
}
