package org.binas.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.binas.ws.UserView;

import exceptions.AlreadyHasBinaException;
import exceptions.BadInitException;
import exceptions.EmailExistsException;
import exceptions.FullStationException;
import exceptions.InvalidEmailException;
import exceptions.InvalidStationException;
import exceptions.NoBinaAvailException;
import exceptions.NoBinaRentedException;
import exceptions.NoCreditException;
import exceptions.UserNotExistsException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

public class BinasManager {	
	// Singleton -------------------------------------------------------------
	private BinasManager() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}	

	/**UDDI URL to search for stations*/
	String uddiURL = null;
	
	public void setUDDIUrl(String uddiUrl) {
		this.uddiURL = uddiUrl;
	}
	
	// Binas bussiness logic
	public UserView addUser(String email) throws InvalidEmailException, EmailExistsException {
		return UsersManager.getInstance().addUser(email);
	}
	
	public User getUser(String email) throws UserNotExistsException{
		return UsersManager.getInstance().getUser(email);
	}
	
	public void rentBina(String stationId, String email) throws AlreadyHasBinaException, InvalidStationException, 
		UserNotExistsException, NoBinaAvailException, NoCreditException{
		StationClient stationC = getStation(stationId);
		User user = this.getUser(email);
		
		try {
			if(user.isHasBina()) {
				throw new AlreadyHasBinaException("Given user already has a bina rented.");
			}
			else{
				org.binas.station.ws.StationView stationView = stationC.getInfo();
				
				if(stationView.getAvailableBinas() == 0) {
					throw new NoBinaAvailException("There are no available binas in the station");
				}
				if(user.getCredit() < 1) {
					throw new NoCreditException("The user" + user.getEmail() + "does't have enough credits");
				}
				
				else {
					synchronized(user){
						stationC.getBina();
						user.setHasBina(true);
						user.substractCredit(1);
					}
				}		
			}
		}
		catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			throw new NoBinaAvailException("There are no available binas in the station");
		}
	}
	
	public void returnBina(String stationId, String email) throws UserNotExistsException, InvalidStationException, 
		NoBinaRentedException, FullStationException{
	
		User user = this.getUser(email);
		StationClient stationC = getStation(stationId);
		
		if(!user.isHasBina()){
			throw new NoBinaRentedException("Given user doesn't have any bina rented.");
		}
		
		try {
			synchronized(user){
				int bonus = stationC.returnBina();
				user.setHasBina(false);
				user.addCredit(bonus);
			}
			
		}
		catch (NoSlotAvail_Exception e) {
			throw new FullStationException("No Slot Available at given Station.");
		}
		
	}
	
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates){
		// check Arguments
		if(numberOfStations < 0){
			return new ArrayList<StationView>();
		}
		if(coordinates == null || coordinates.getX() == null || coordinates.getY() == null){
			return new ArrayList<StationView>();
		}
				
		List<StationClient> listStations = this.getAllStations();
		ArrayList<StationView> view = new ArrayList<StationView>();
		for (StationClient stationClient: listStations) {
			view.add(stationViewSetter(stationClient.getInfo()));
		}	

		view.sort(new StationComparator(coordinates));
		
		if ( view.size() <= numberOfStations  ) {
			return view;
		}
		else {
			return view.subList(0, numberOfStations);
		}		
	}
	
	public void init(int newBeginCredit) throws BadInitException{
		UsersManager.getInstance().init(newBeginCredit);
	}
	
	public void initStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInitException{
		
		if(stringNullOrEmpty(stationId)){
			throw new BadInitException("StationId can not be null or empty.");
		}
		if(capacity < 0){
			throw new BadInitException("Capacity can not be negative.");
		}
		if(returnPrize < 0){
			throw new BadInitException("Prize can not be negative.");
		}
		
		try {
			StationClient client = new StationClient(this.uddiURL, stationId);
			
			client.testInit(x, y, capacity, returnPrize);
		} catch (StationClientException e) {
			throw new BadInitException(e.getMessage());
		} catch (org.binas.station.ws.BadInit_Exception e) {
			throw new BadInitException(e.getMessage());
		}
	}
	
	public String ping(String inputMessage, String wsName) {
		if(inputMessage == null || inputMessage.trim().equals("")){
			inputMessage = "friend";
		}
		
		String result = "Hello " + inputMessage + " from " + wsName + "\n";

		List<StationClient> listStations = this.getAllStations();
		result += "Founded " + listStations.size() + " stations.\n";
		
		for(StationClient stationClient : listStations){
			result += "[Pinging Station = " + stationClient.getWsName() + "][Answer] ";
			result += stationClient.testPing(inputMessage) + "\n";
		}
			
		return result;
	}
	
	public void reset(){
		UsersManager.getInstance().reset();
		List<StationClient> listStations = this.getAllStations();
		for(StationClient stationClient : listStations){
			stationClient.testClear();
		}
	}
	
	// UDDI
	public StationClient getStation(String stationId) throws InvalidStationException{
		if(stringNullOrEmpty(stationId)){
			throw new InvalidStationException("Invalid Station given, can not be null or empty.");
		}
		try {
			return new StationClient(this.uddiURL, stationId);			
		} 
		catch (StationClientException e) {
			throw new InvalidStationException("Invalid Station given.");
		}
	}
	
	private List<StationClient> getAllStations() {
		UDDINaming uddiNaming;
		Collection<UDDIRecord> uddiList;
		List<StationClient> listStations = new ArrayList<StationClient>();
		
		try {
			uddiNaming = new UDDINaming(this.uddiURL);
			uddiList = uddiNaming.listRecords("A47_Station%");
		} catch (UDDINamingException e) {
			System.err.printf("Caught exception connecting to UDDI: %s%n", e);
			return listStations;
		}
		
		for(UDDIRecord record : uddiList){
			try{
				StationClient stationClient = new StationClient(record.getUrl());
				stationClient.setWsName(record.getOrgName());
				listStations.add(stationClient);
			}
			catch (StationClientException e) {
				System.err.printf("Caught exception creating StationClientException: %s%n", e);
			}
		}
		
		return listStations;
	}
	
	
	// Auxiliary Functions
	private boolean stringNullOrEmpty(String string){
		if(string == null || string.trim().equals("")){
			return true;
		}
		return false;
	}
	
	private StationView stationViewSetter(org.binas.station.ws.StationView stationView) {
		StationView svBinas = new StationView();
		svBinas.setAvailableBinas(stationView.getAvailableBinas());
		
		svBinas.setCapacity(stationView.getCapacity());
		CoordinatesView cv = new CoordinatesView();
		cv.setX(stationView.getCoordinate().getX());
		cv.setY(stationView.getCoordinate().getY());
		svBinas.setCoordinate(cv);
		
		svBinas.setFreeDocks(stationView.getFreeDocks());
		svBinas.setId(stationView.getId());
		svBinas.setTotalGets(stationView.getTotalGets());
		svBinas.setTotalReturns(stationView.getTotalReturns());
		return svBinas;	
	}

}
