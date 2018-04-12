package org.binas.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ListStationsIT extends BaseIT {
	private static CoordinatesView coordinates = new CoordinatesView();
	private static final int USER_X = 10;
	private static final int USER_Y = 10;
	
	// Station 1
	private static final String STATION_1 = "A47_Station1";
	private static final int STATION_1_X = 22;
	private static final int STATION_1_Y = 7;
	private static final int STATION_1_CAPACITY = 6;
	private static final int STATION_1_BONUS = 2;
	
	// Station 2
	private static final String STATION_2 = "A47_Station2";
	private static final int STATION_2_X = 80;
	private static final int STATION_2_Y = 20;
	private static final int STATION_2_CAPACITY = 12;
	private static final int STATION_2_BONUS = 1;
	
	// Station 2
	private static final String STATION_3 = "A47_Station3";
	private static final int STATION_3_X = 50;
	private static final int STATION_3_Y = 50;
	private static final int STATION_3_CAPACITY = 20;
	private static final int STATION_3_BONUS = 0;
	
	private static SortedMap<Integer,Integer> orderedMap = new TreeMap<Integer,Integer>();
	private static final Set<Integer> sorted = sortStations();
	
	
	@Before
	public void setUp() throws BadInit_Exception{
		client.testInitStation(STATION_1, STATION_1_X, STATION_1_Y, STATION_1_CAPACITY, STATION_1_BONUS);
		client.testInitStation(STATION_2, STATION_2_X, STATION_2_Y, STATION_2_CAPACITY, STATION_2_BONUS);
		client.testInitStation(STATION_3, STATION_3_X, STATION_3_Y, STATION_3_CAPACITY, STATION_3_BONUS);
		
		coordinates.setX(USER_X);
		coordinates.setY(USER_Y);
		
	}
	
	@Test
	public void sucess(){
		ArrayList<StationView> listStations = (ArrayList<StationView>) client.listStations(3, coordinates);
		
		assertEquals(3, listStations.size());
		
		String first = String.format("A47_Station%d", sorted.toArray()[0]);
		String second = String.format("A47_Station%d", sorted.toArray()[1]);
		String third = String.format("A47_Station%d", sorted.toArray()[2]);
		
		assertEquals(first, listStations.get(0));
		assertEquals(second, listStations.get(1));
		assertEquals(third, listStations.get(2));
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
	
	private static Set<Integer> sortStations(){
		orderedMap.put(distanceSquaredBetweenPoints(STATION_1_X, STATION_1_Y, USER_X, USER_Y), 1);
		orderedMap.put(distanceSquaredBetweenPoints(STATION_2_X, STATION_2_Y, USER_X, USER_Y), 1);
		orderedMap.put(distanceSquaredBetweenPoints(STATION_3_X, STATION_3_Y, USER_X, USER_Y), 1);
		return orderedMap.keySet();
	}
	
	private static int distanceSquaredBetweenPoints(int x1, int y1, int x2, int y2){
		return (int) (Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
}
