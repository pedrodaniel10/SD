package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Test;

public class GetInfoIT extends BaseIT {
	
	@Test
	public void sucess() throws BadInit_Exception, NoBinaAvail_Exception, NoSlotAvail_Exception{
		client.testInit(3, 3, 50, 0);
		for(int i = 0; i < 40; i++){
			client.getBina();
		}
		
		for(int i = 0; i < 30; i++){
				client.returnBina();
		}
		
		StationView stationView = client.getInfo();
		
		assertEquals(40, stationView.getTotalGets());
		assertEquals(30, stationView.getTotalReturns());
		assertEquals(50, stationView.getCapacity());
		assertEquals(10, stationView.getFreeDocks());
		assertEquals(40, stationView.getAvailableBinas());
		assertEquals(3, stationView.getCoordinate().getX());
		assertEquals(3, stationView.getCoordinate().getY());
	}

	@After
	public void tearDown(){
		client.testClear();
	}
}
