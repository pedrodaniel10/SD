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
import org.binas.domain.UsersManager;
import org.binas.station.ws.AccountView;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.InvalidCredit_Exception;
import org.binas.station.ws.InvalidFormatEmail_Exception;
import org.binas.station.ws.SetBalanceResponse;
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
			return binasManager.getCredit(email);
		} catch (InvalidEmailException | UserNotExistsException e) {
			ExceptionsHelper.throwUserNotExists(e.getMessage());
		}
		return -1;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		try {
			return binasManager.addUser(email);
		} catch (InvalidEmailException e) {
			ExceptionsHelper.throwInvalidEmail(e.getMessage());
		} catch (EmailExistsException e) {
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
	
	private UserView newUserView(String email, int credit, boolean hasBina) {
		UserView userView= new UserView();
		
		userView.setEmail(email);
		userView.setCredit(credit);
		userView.setHasBina(hasBina);
		
		return userView;
	}
}

