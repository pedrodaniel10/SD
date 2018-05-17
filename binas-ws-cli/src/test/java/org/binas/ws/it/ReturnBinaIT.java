package org.binas.ws.it;

import static org.junit.Assert.*;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReturnBinaIT extends BaseIT {
	
	private static final String stationID1 = "A47_Station1";
	private static final String stationID2 = "A47_Station2";
	private static final String stationIDInvalid = "CXX";
	private static final String email = "alice@A47.binas.org";
	private static final String emailNotActive = "sd@tecnico";
	
	//(º)	consegue retornar uma bicicleta numa estacao com sucesso
	//(º)	consegue fazer 2 returns com sucesso
	//(º)	consegue fazer return com sucesso numa estacao vazia
	//(º)	FullStation_Exception - levanta bina de uma estacao e tentra entregar numa que esteja cheia
	//(º)	InvalidStatio_Exception - levanta a bina de uma estacao e tenta entregar numa estacao que nao existe
	//(º)	InvalidStation - levanta bina e tenta entregar numa estacao que nao se encaixano nome
	//(º)	InvalidStation_Exception - nome de station null
	//(º)	InvalidStation_Exception - nome string vazia
	//(º)	UserNotExists - return uma bina, chamar o email que nao esta ativo
	//(º)	UserNotExists - return bina com email vazio
	//(º)	UserNotExists - return bina com email null
	//(º)	NoBinaRented - tenta levantar bina antes de alugar
	//(º)	NoBinaRented - aluga e devolve uma bina e tenta devolver logo a seguir outra que nao tem na mesma estacao
	//(º)	NoBinaRented - aluga bina e devolve e tenta devolver logo de seguida numa estacao diferente
	
	
	
	@Before
	public void setUp() throws EmailExists_Exception, InvalidEmail_Exception, BadInit_Exception {
		client.testInit(10);
		client.activateUser(email);
	}
	
	@Test
	public void sucess() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, 
		NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);
		
		StationView station = client.getInfoStation(stationID1);

		assertEquals(0, station.getFreeDocks());
		assertEquals(1, station.getTotalReturns());
		assertEquals(20, station.getAvailableBinas());
		assertEquals(19, client.getCredit(email));
	}
	
	@Test
	public void twoReturnSameStationSucess() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, 
		NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);
		
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);
		
		StationView station = client.getInfoStation(stationID1);
		
		assertEquals(0, station.getFreeDocks());
		assertEquals(2, station.getTotalReturns());
		assertEquals(20, station.getAvailableBinas());
		assertEquals(28, client.getCredit(email));
	}
	
	@Test
	public void twoReturnsDiffStations() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, 
		NoBinaRented_Exception, EmailExists_Exception, InvalidEmail_Exception{
		client.testInitStation(stationID1, 22, 7, 6, 2);
		client.testInitStation(stationID2, 80, 20, 12, 1);
		client.activateUser("binas@test");
		
		client.rentBina(stationID1, email);
		client.rentBina(stationID2, "binas@test");
		client.returnBina(stationID2, email);
		client.returnBina(stationID1, "binas@test");
		
		StationView station1 = client.getInfoStation(stationID1);
		
		assertEquals(0, station1.getFreeDocks());
		assertEquals(1, station1.getTotalReturns());
		assertEquals(6, station1.getAvailableBinas());
		
		StationView station2 = client.getInfoStation(stationID2);
		
		assertEquals(0, station2.getFreeDocks());
		assertEquals(1, station2.getTotalReturns());
		assertEquals(12, station2.getAvailableBinas());
		
		assertEquals(10, client.getCredit(email));
		assertEquals(11, client.getCredit("binas@test"));
	}
	
	@Test (expected = AlreadyHasBina_Exception.class)
	public void NoSlotsAvailable() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		client.testInitStation(stationID2, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		try {
			client.returnBina(stationID2, email);
			fail();
		} 
		catch (FullStation_Exception e) {
			StationView station1 = client.getInfoStation(stationID1);
			
			assertEquals(1, station1.getFreeDocks());
			assertEquals(0, station1.getTotalReturns());
			assertEquals(19, station1.getAvailableBinas());
			
			StationView station2 = client.getInfoStation(stationID2);
			
			assertEquals(0, station2.getFreeDocks());
			assertEquals(0, station2.getTotalReturns());
			assertEquals(20, station2.getAvailableBinas());
			
			assertEquals(9, client.getCredit(email));
			
			client.rentBina(stationID1, email);
		}	
		
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void InvalidStationID() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, 
		NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina(stationIDInvalid, email);	
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void InvalidStationNull() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, 
		NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina(null, email);	
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void InvalidStationEmptyID() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina("", email);	
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void InvalidStationBlankID() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina("  ", email);	
	}
	
	@Test (expected = UserNotExists_Exception.class)
	public void UserNotRegisteredD() throws BadInit_Exception, InvalidStation_Exception, NoCredit_Exception, 
		UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{		
		client.returnBina(stationID1, emailNotActive);			
	}
	
	@Test (expected = UserNotExists_Exception.class)
	public void UserNull() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.returnBina(stationID1, null);	
	}
	
	@Test (expected = UserNotExists_Exception.class)
	public void UserEmpty() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.returnBina(stationID1, "");	
	}
	
	@Test (expected = UserNotExists_Exception.class)
	public void UserBlank() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.returnBina(stationID1, "  ");	
	}
	
	@Test
	public void NoneRented() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, AlreadyHasBina_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		try {
			client.returnBina(stationID1, email);
			fail();
		} 
		catch (NoBinaRented_Exception e) {
			StationView station = client.getInfoStation(stationID1);
			
			assertEquals(0, station.getFreeDocks());
			assertEquals(0, station.getTotalReturns());
			assertEquals(20, station.getAvailableBinas());
			assertEquals(10, client.getCredit(email));
		}
	}
	
	@Test
	public void LreadyReturnedSameStation() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, 
		NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, AlreadyHasBina_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		try{
			client.rentBina(stationID1, email);
			client.returnBina(stationID1, email);
			client.returnBina(stationID1, email);
			fail();
		}
		catch (NoBinaRented_Exception e) {
			StationView station = client.getInfoStation(stationID1);
			
			assertEquals(0, station.getFreeDocks());
			assertEquals(1, station.getTotalReturns());
			assertEquals(20, station.getAvailableBinas());
			assertEquals(19, client.getCredit(email));
		}
	}
	
	@Test
	public void LreadyReturnedDifferentStation() throws BadInit_Exception, InvalidStation_Exception, 
		NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, 
		AlreadyHasBina_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		client.testInitStation(stationID2, 5, 5, 20, 10);
		
		try{
			client.rentBina(stationID1, email);
			client.returnBina(stationID1, email);
			client.returnBina(stationID2, email);
			fail();
		}
		catch (NoBinaRented_Exception e) {
			StationView station = client.getInfoStation(stationID1);
			
			assertEquals(0, station.getFreeDocks());
			assertEquals(1, station.getTotalReturns());
			assertEquals(20, station.getAvailableBinas());
			
			StationView station2 = client.getInfoStation(stationID2);
			
			assertEquals(0, station2.getFreeDocks());
			assertEquals(0, station2.getTotalReturns());
			assertEquals(20, station2.getAvailableBinas());
			
			assertEquals(19, client.getCredit(email));
		}
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
	
}


