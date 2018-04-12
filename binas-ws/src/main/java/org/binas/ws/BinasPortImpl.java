package org.binas.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.jws.WebService;

import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;

import exceptions.Exceptions;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

@WebService(endpointInterface = "org.binas.ws.BinasPortType",
wsdlLocation = "binas.1_0.wsdl",
name ="BinasService",
portName = "BinasPort",
targetNamespace="http://ws.binas.org/",
serviceName = "BinasService"
)
public class BinasPortImpl implements BinasPortType {

	private BinasManager binasManager;

	public BinasPortImpl(BinasManager binasManager) {
		this.binasManager = binasManager;
	}

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		
		class StationComparator implements Comparator<StationView> {

			int x = coordinates.getX();
			int y = coordinates.getY();
			
		    @Override
		    public int compare(StationView sv1, StationView sv2) {
		    		int x1 = sv1.getCoordinate().getX();
				int y1 = sv1.getCoordinate().getY();
				int x2 = sv2.getCoordinate().getX();
				int y2 = sv2.getCoordinate().getY();
		    		int distance1 = (x1-this.x)*(x1-this.x) + (y1-this.y)*(y1-this.y);;
		    		int distance2 = (x2-this.x)*(x2-this.x) + (y2-this.y)*(y2-this.y);;
		        return distance1 - distance2;
		    }
		}
		
		List<StationClient> listStations = this.getAllStations();
		ArrayList<StationView> view = new ArrayList<StationView>();
		for (StationClient stationClient: listStations) {
			view.add(stationViewSetter(stationClient.getInfo()));
		}	
		
		view.sort(new StationComparator());
		
		if ( view.size() <= numberOfStations  ) {
			return view;
		}
		else {
			return view.subList(0, numberOfStations);
		}		
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {

		if(stationId == null || stationId.trim().equals("")){
			Exceptions.throwInvalidStation("StationId cannot be null or blank.");
		}
		
		StationClient stationClient;
		String uddiURL;
		try {
			uddiURL = this.binasManager.getUDDIUrl();
			stationClient = new StationClient(uddiURL, stationId);
			org.binas.station.ws.StationView stationView = stationClient.getInfo();
			
			return stationViewSetter(stationView);	
			
		} 
		catch (StationClientException e) {
			Exceptions.throwInvalidStation("An error has occured while connecting to the Station:" + stationId);
		}
		
		return null;
	}


	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		User user = User.getUser(email);
		return user.getCredit();
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		User user = new User(email);
		
		return user.getUserView();
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,
			NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		
		try {
			User user = User.getUser(email);
			
			if(user.isHasBina()) {
				Exceptions.throwAlreadyHasBina("Given user already has a bina rented.");
			}
			else{
				StationClient stationC = new StationClient(this.binasManager.getUDDIUrl(), stationId);
				org.binas.station.ws.StationView stationView = stationC.getInfo();
				
				if(stationView.getAvailableBinas() == 0) {
					Exceptions.throwNoBinaAvail("There are no available binas in the station");
				}
				if(user.getCredit() < 1) {
					Exceptions.throwNoCredit("The user" + user.getEmail() + "does't have enough credits");
				}
				
				else {
					stationC.getBina();
					user.setHasBina(true);
					user.substractCredit(1);
				}		
			}
		}
		catch (StationClientException e) {
			Exceptions.throwInvalidStation("Invalid Station Given.");
		} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			Exceptions.throwNoBinaAvail("There are no available binas in the station");
		}
		
		
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		
		try {
			User user = User.getUser(email);
			
			if(!user.isHasBina()) {
				Exceptions.throwNoBinaRented("Given user doesn't have any bina rented.");
			}
			else{
				user.setHasBina(false);
				StationClient stationC = new StationClient(this.binasManager.getUDDIUrl(), stationId);

				int bonus = stationC.returnBina();
				user.addCredit(bonus);
			}
		}
		catch (StationClientException e) {
			Exceptions.throwInvalidStation("Invalid Station Given.");
		} 
		catch (NoSlotAvail_Exception e) {
			Exceptions.throwFullStation("No Slot Available at given Station.");
		}
		
	}

//	Tests functions
	@Override
	public String testPing(String inputMessage) {
		if(inputMessage == null || inputMessage.trim().equals("")){
			inputMessage = "friend";
		}
		
		String result = "Hello " + inputMessage + " from " + binasManager.getWsName() + "\n";

		List<StationClient> listStations = this.getAllStations();
		result += "Founded " + listStations.size() + " stations.\n";
		
		for(StationClient stationClient : listStations){
			result += "[Pinging Station = " + stationClient.getWsName() + "][Answer] ";
			result += stationClient.testPing(inputMessage) + "\n";
		}
			
		return result;
	}

	@Override
	public void testClear() {
		User.clear();
		List<StationClient> listStations = this.getAllStations();
		for(StationClient stationClient : listStations){
			stationClient.testClear();
		}
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws BadInit_Exception {
		try {
			StationClient client = new StationClient(binasManager.getUDDIUrl(), stationId);
			
			client.testInit(x, y, capacity, returnPrize);
		} catch (StationClientException e) {
			Exceptions.throwBadInit(e.getMessage());
		} catch (org.binas.station.ws.BadInit_Exception e) {
			Exceptions.throwBadInit(e.getMessage());
		}
		
		
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		User.init(userInitialPoints);
		
	}
	
//	<-- Auxiliary functions -->
	private List<StationClient> getAllStations() {
		UDDINaming uddiNaming;
		Collection<UDDIRecord> uddiList;
		List<StationClient> listStations = new ArrayList<StationClient>();
		
		try {
			uddiNaming = this.binasManager.getUddiNaming();
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
