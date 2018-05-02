package org.binas.ws;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.jws.WebService;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.binas.domain.BinasManager;
import org.binas.station.ws.AccountView;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.InvalidFormatEmail_Exception;
import org.binas.station.ws.UserDoesNotExists_Exception;
import org.binas.station.ws.cli.StationClient;

import exceptions.AlreadyHasBinaException;
import exceptions.BadInitException;
import exceptions.EmailExistsException;
import exceptions.ExceptionsHelper;
import exceptions.FullStationException;
import exceptions.InvalidEmailException;
import exceptions.InvalidStationException;
import exceptions.NoBinaAvailException;
import exceptions.NoBinaRentedException;
import exceptions.NoCreditException;
import exceptions.UserNotExistsException;

@WebService(endpointInterface = "org.binas.ws.BinasPortType",
wsdlLocation = "binas.1_0.wsdl",
name ="BinasService",
portName = "BinasPort",
targetNamespace="http://ws.binas.org/",
serviceName = "BinasService"
)
public class BinasPortImpl implements BinasPortType {
	/** Endpoint Manager for binas*/
	private BinasEndpointManager binasEndpointManager;
	
	/** Binas Manager */
	private BinasManager binasManager = BinasManager.getInstance();
	
	/**
	 * Constructor BinasPortImpl
	 */
	public BinasPortImpl(BinasEndpointManager binasEndpointManager) {
		this.binasEndpointManager = binasEndpointManager;
	}

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		return binasManager.listStations(numberOfStations, coordinates);
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		StationClient stationClient = null;
		try {
			stationClient = binasManager.getStation(stationId);
		} catch (InvalidStationException e) {
			ExceptionsHelper.throwInvalidStation(e.getMessage());
		}
		org.binas.station.ws.StationView stationView = stationClient.getInfo();
		
		return stationViewSetter(stationView);	
		
	}


	@Override
	public int getCredit(String email) throws UserNotExists_Exception {		
		try {
			checkEmail(email);
			return getBalance(email);
		} catch (InvalidEmailException | UserNotExistsException e) {
			ExceptionsHelper.throwUserNotExists(e.getMessage());
		}
		return -1;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		try {
			return binasManager.addUser(email);
		} 
		catch(InvalidEmailException e) {
			ExceptionsHelper.throwInvalidEmail(e.getMessage());
		}
		catch(EmailExistsException e) {
			ExceptionsHelper.throwEmailExists(e.getMessage());
		}
		return null;
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,
			NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		try {
			binasManager.rentBina(stationId, email);
		} catch (AlreadyHasBinaException e) {
			ExceptionsHelper.throwAlreadyHasBina(e.getMessage());
		} catch (InvalidStationException e) {
			ExceptionsHelper.throwInvalidStation(e.getMessage());
		} catch (UserNotExistsException e) {
			ExceptionsHelper.throwUserNotExists(e.getMessage());
		} catch (NoBinaAvailException e) {
			ExceptionsHelper.throwNoBinaAvail(e.getMessage());
		} catch (NoCreditException e) {
			ExceptionsHelper.throwNoCredit(e.getMessage());
		}
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		try {
			binasManager.returnBina(stationId, email);
		} catch (UserNotExistsException e) {
			ExceptionsHelper.throwUserNotExists(e.getMessage());
		} catch (InvalidStationException e) {
			ExceptionsHelper.throwInvalidStation(e.getMessage());
		} catch (NoBinaRentedException e) {
			ExceptionsHelper.throwNoBinaRented(e.getMessage());
		} catch (FullStationException e) {
			ExceptionsHelper.throwFullStation(e.getMessage());
		}
	}

//	Tests functions
	@Override
	public String testPing(String inputMessage) {
		return binasManager.ping(inputMessage, binasEndpointManager.getWsName());
	}

	@Override
	public void testClear() {
		binasManager.reset();
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws BadInit_Exception {
		try {
			binasManager.initStation(stationId, x, y, capacity, returnPrize);
		} catch (BadInitException e) {
			ExceptionsHelper.throwBadInit(e.getMessage());
		}
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		try {
			binasManager.init(userInitialPoints);
		} catch (BadInitException e) {
			ExceptionsHelper.throwBadInit(e.getMessage());
		}
	}
	
//	<-- Auxiliary functions -->
	
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
	
	
	private int getBalance(String email) throws InvalidEmailException, UserNotExistsException {
		int numberOfReplics = binasEndpointManager.getReplicsNumber();
		HashMap<String, StationClient> replics = new HashMap<String, StationClient>();
		//search replica and try 3 times to connect(on fail)
		for(int i = 1; i <= numberOfReplics; i++){
			int numberOfTries = 0;
			while(numberOfTries != 3) {
				try {
					String wsName = "A47_Station" + i;
					replics.put(wsName, binasManager.getStation(wsName));
					break;
				} 
				catch (InvalidStationException e) {
					numberOfTries++;
				}
			}
		}

		HashMap<String, Response<GetBalanceResponse>> responses = new HashMap<>();
		int numberOfResponses = 0;
		
		//Make requests
		for(String stationsName  : replics.keySet()) {
			StationClient client = replics.get(stationsName);
			responses.put(stationsName, client.getBalanceAsync(email));
		}
		
		//Get quorum
		ArrayList<AccountView> accountViews = new ArrayList<>();
		while(numberOfResponses != (numberOfReplics/2 + 1)) {
			for(String stationsName : responses.keySet()) {
				if(responses.get(stationsName).isDone()) {
					try {
						accountViews.add(responses.get(stationsName).get().getAccountInfo());
						responses.remove(stationsName);
						numberOfResponses++;
					} catch(ExecutionException e){
						Throwable cause = e.getCause();
						if(cause != null && cause instanceof InvalidFormatEmail_Exception){
							throw new InvalidEmailException(e.getMessage());
						}
						else if(cause != null && cause instanceof UserDoesNotExists_Exception){
							throw new UserNotExistsException(e.getMessage());
						}
						else{
							//try again, send the query
							responses.remove(stationsName);
							responses.put(stationsName, replics.get(stationsName).getBalanceAsync(email));
						}
					} catch (InterruptedException e) {
						//try again, send the query
						responses.remove(stationsName);
						responses.put(stationsName, replics.get(stationsName).getBalanceAsync(email));
					} catch(WebServiceException wse) {
		                Throwable cause = wse.getCause();
		                if (cause != null && cause instanceof SocketTimeoutException) {
		                	//try again, send the query
							responses.remove(stationsName);
							responses.put(stationsName, replics.get(stationsName).getBalanceAsync(email));
		                }
		            }
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//do nothing
			}
		}

		//give answer by tag
		AccountView latestVersion = null;
		for(AccountView accountView : accountViews){
			if(latestVersion == null){
				latestVersion = accountView;
			}
			else if(accountView.getTag() > latestVersion.getTag()){
				latestVersion = accountView;
			}
			else if(accountView.getTag() == latestVersion.getTag()){
				if(accountView.getClientID() > latestVersion.getClientID()){
					latestVersion = accountView;
				}
			}
		}
		return latestVersion.getCredit();
	}
	
	
	private boolean setBalance(String email, int credit, int tag, int clientID) {
		int numberOfReplics = binasEndpointManager.getReplicsNumber();
		HashMap<String, StationClient> replics = new HashMap<String, StationClient>();
		//search replica and try 3 times to connect(on fail)
		for(int i = 1; i <= numberOfReplics; i++){
			int numberOfTries = 0;
			while(numberOfTries != 3) {
				try {
					String wsName = "A47_Station" + i;
					replics.put(wsName, binasManager.getStation(wsName));
					break;
				} 
				catch (InvalidStationException e) {
					numberOfTries++;
				}
			}
		}

		HashMap<String, Response<SetBalanceResponse>> responses = new HashMap<>();
		int numberOfResponses = 0;
		
		//Make requests
		for(String stationsName  : replics.keySet()) {
			StationClient client = replics.get(stationsName);
			responses.put(stationsName, client.setBalanceAsync(email, credit, tag, clientID));
		}
		
		//Get quorum
		ArrayList<Boolean> booleanArray = new ArrayList<>();
		while(numberOfResponses != (numberOfReplics/2 + 1)) {
			for(String stationsName : responses.keySet()) {
				if(responses.get(stationsName).isDone()) {
					try {
						booleanArray.add(true); //isto esta bem?
						responses.remove(stationsName);
						numberOfResponses++;
					} catch (InterruptedException | ExecutionException e) {
						//try again, send the query
						responses.remove(stationsName);
						responses.put(stationsName, replics.get(stationsName).setBalanceAsync(email, credit, tag, clientID));
					} catch(WebServiceException wse) {
		                Throwable cause = wse.getCause();
		                if (cause != null && cause instanceof SocketTimeoutException) {
		                	//try again, send the query
							responses.remove(stationsName);
							responses.put(stationsName, replics.get(stationsName).setBalanceAsync(email, credit, tag, clientID));
		                }
		            }
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//do nothing
			}
		}

		//give answer by tag
		boolean latestVersion = false;
		for(boolean b : booleanArray){
			if(latestVersion == false){
				latestVersion = b;
			}
			else if(accountView.getTag() > latestVersion.getTag()){
				latestVersion = accountView;
			}
			else if(accountView.getTag() == latestVersion.getTag()){
				if(accountView.getClientID() > latestVersion.getClientID()){
					latestVersion = accountView;
				}
			}
		}
		return latestVersion.getCredit();
	}
	
	private void checkEmail(String email) throws InvalidEmailException {
		final String regex = "^(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)@(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)";
		
		if(email == null || email.trim().equals("")){
			throw new InvalidEmailException("The email can not be null or empty.");
		}
		if(!email.matches(regex)){
			throw new InvalidEmailException("The email " + email + " format is invalid.");
		}	
	}
}

