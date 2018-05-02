package org.binas.station.ws;

import javax.jws.WebService;

import org.binas.station.domain.Coordinates;
import org.binas.station.domain.Station;
import org.binas.station.domain.User;
import org.binas.station.domain.UsersManager;
import org.binas.station.domain.exception.BadInitException;
import org.binas.station.domain.exception.ExceptionsHelper;
import org.binas.station.domain.exception.InvalidCreditException;
import org.binas.station.domain.exception.InvalidFormatEmailException;
import org.binas.station.domain.exception.NoBinaAvailException;
import org.binas.station.domain.exception.NoSlotAvailException;
import org.binas.station.domain.exception.UserDoesNotExistsException;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
// TODO
@WebService(endpointInterface = "org.binas.station.ws.StationPortType",
wsdlLocation = "station.2_0.wsdl",
name ="StationService",
portName = "StationPort",
targetNamespace="http://ws.station.binas.org/",
serviceName = "StationService"
)
public class StationPortImpl implements StationPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private StationEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public StationPortImpl(StationEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------

	 /** Retrieve information about station. */
	@Override
	public StationView getInfo() {
		Station station = Station.getInstance();
		synchronized(station){	
			return this.buildStationView(Station.getInstance());
		}
	}
	
	 /** Return a bike to the station. */
	@Override
	public int returnBina() throws NoSlotAvail_Exception {
		Station station = Station.getInstance();
		
		try {
			return station.returnBina();
		} catch (NoSlotAvailException e) {
			ExceptionsHelper.throwNoSlotAvail(e.getMessage());
			return -1;
		}
	}
	
	 /** Take a bike from the station. */
	@Override
	public void getBina() throws NoBinaAvail_Exception {
		Station station = Station.getInstance();
		
		try {
			station.getBina();
		} catch (NoBinaAvailException e) {
			ExceptionsHelper.throwNoBinaAvail(e.getMessage());
		}
	}
	
	@Override
	public AccountView getBalance(String userEmail) throws UserDoesNotExists_Exception, InvalidFormatEmail_Exception {
		try {
			return newAccountView(UsersManager.getInstance().getUser(userEmail));
		} 
		catch (UserDoesNotExistsException e) {
			ExceptionsHelper.throwUserDoesNotExists(e.getMessage());
		} 
		catch (InvalidFormatEmailException e) {
			ExceptionsHelper.throwInvalidFormatEmail(e.getMessage());
		}
		return null;
	}

	private AccountView newAccountView(User user) {		
		AccountView av = new AccountView();
		av.setClientID(user.getClientID());
		av.setTag(user.getTag());
		av.setCredit(user.getCredit());
		
		return av;		
	}

	@Override
	public boolean setBalance(String userEmail, int credit, int tag, int clientID) throws InvalidCredit_Exception, InvalidFormatEmail_Exception{
			UsersManager userManager = UsersManager.getInstance();
			try {
				User user = userManager.getUser(userEmail);
				if ( tag > user.getTag() || (tag == user.getTag() && clientID > user.getClientID()) ) {
					user.setClientID(clientID);
					user.setCredit(credit);
					user.setTag(tag);
				}
			}
			catch(UserDoesNotExistsException e) {
					try {
						userManager.addUser(userEmail, credit, tag, clientID);
					} catch (InvalidFormatEmailException e1) {
						ExceptionsHelper.throwInvalidFormatEmail(e.getMessage());
					} catch (InvalidCreditException e1) {
						ExceptionsHelper.throwInvalidCredit(e.getMessage());
					}
			}
			catch(InvalidFormatEmailException e){
				ExceptionsHelper.throwInvalidFormatEmail(e.getMessage());
			}
			catch(InvalidCreditException e){
				ExceptionsHelper.throwInvalidCredit(e.getMessage());
			}
			return true;
	}

	// Test Control operations -----------------------------------------------

	 /** Diagnostic operation to check if service is running. */
	@Override
	public String testPing(String inputMessage) {
	 // If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";
	
	 // If the station does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Station";
	
	 // Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}
	
	 /** Return all station variables to default values. */
	@Override
	public void testClear() {
		Station.getInstance().reset();
		UsersManager.getInstance().reset();
	}
	
	 /** Set station variables with specific values. */
	@Override
	public void testInit(int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		try {
			Station.getInstance().init(x, y, capacity, returnPrize);
		} catch (BadInitException e) {
			ExceptionsHelper.throwBadInit("Invalid initialization values!");
		}
	}
	

	// View helpers ----------------------------------------------------------

	 /** Helper to convert a domain station to a view. */
	private StationView buildStationView(Station station) {
		StationView view = new StationView();
		view.setId(station.getId());
		view.setCoordinate(buildCoordinatesView(station.getCoordinates()));
		view.setCapacity(station.getMaxCapacity());
		view.setTotalGets(station.getTotalGets());
		view.setTotalReturns(station.getTotalReturns());
		view.setFreeDocks(station.getFreeDocks());
		view.setAvailableBinas(station.getAvailableBinas());
		return view;
	}
	//
	// /** Helper to convert a domain coordinates to a view. */
	private CoordinatesView buildCoordinatesView(Coordinates coordinates) {
		CoordinatesView view = new CoordinatesView();
		view.setX(coordinates.getX());
		view.setY(coordinates.getY());
		return view;
	}

}
