package org.binas.ws;

import java.util.Collection;
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
		// TODO Auto-generated method stub		
		return null;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {

		StationClient stationClient;
		String uddiURL;
		try {
			uddiURL = this.binasManager.getUDDIUrl();
			stationClient = new StationClient(uddiURL, stationId);
			//Falta retornar a stationview correta
			return stationClient.getInfo();
		
			
		} catch (UDDINamingException e) {
			System.err.printf("Caught exception: %s%n", e);
		} catch (StationClientException e) {
			System.err.printf("Caught exception creating StationClientException: %s%n", e);
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
		
		
	}

	@Override
	public void returnBina(String stationId, String email)
	//falta aplicar recompensa!!!!!!!!!!
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		
		try {
			User user = User.getUser(email);
			
			if(!user.isHasBina()) {
				Exceptions.throwNoBinaRented("Given user doesn't have any bine rented.");
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

	@Override
	public String testPing(String inputMessage) {
		UDDINaming uddiNaming;
		Collection<UDDIRecord> uddiList;
		String result = "";
		try {
			uddiNaming = this.binasManager.getUddiNaming();
			uddiList = uddiNaming.listRecords("A47_Station%");
			
			result += "Founded " + uddiList.size() + " stations.\n";
			
			for(UDDIRecord record : uddiList){
				result += "[Pinging Station = " + record.getOrgName() + "][Answer] ";
				StationClient stationClient = new StationClient(record.getUrl());
				result += stationClient.testPing(inputMessage) + "\n";
			}
			
		} catch (UDDINamingException e) {
			System.err.printf("Caught exception: %s%n", e);
		} catch (StationClientException e) {
			System.err.printf("Caught exception creating StationClientException: %s%n", e);
		}
		
		return result;
	}

	@Override
	public void testClear() {
		User.clear();
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
		binasManager.init(userInitialPoints);
		
	}

}
