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
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class ReturnBinaIT extends BaseIT {
	
	String stationID1 = "A47_Station1";
	String stationID2 = "A47_Station2";
	String stationIDInvalid = "CXX";
	String stationID3 = "A47_Station3";
	
	String email = "tecnico@sd";
	String emailNotActive = "sd@tecnico";
	
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
	public void setUp() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser(email);
	}
	
	@Test
	public void sucess() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(this.stationID1, 5, 5, 20, 10);
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);

		assertEquals(20, client.getInfoStation(stationID1).getAvailableBinas());
		assertEquals(19, client.getCredit(email));
	}
	
	@Test
	public void sucess2() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(this.stationID1, 5, 5, 20, 10);
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);
		
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);
		
		assertEquals(20, client.getInfoStation(stationID1).getAvailableBinas());
		assertEquals(28, client.getCredit(email));
	}
	
	@Test
	public void sucess3() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(this.stationID1, 5, 5, 1, 10);
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);
		
		assertEquals(1, client.getInfoStation(stationID1).getAvailableBinas());
		assertEquals(19, client.getCredit(email));
	}
	
	@Test (expected = FullStation_Exception.class)
	public void NoSlotsAvailable() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		client.testInitStation(stationID2, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina(stationID2, email);	
		
		assertEquals(9, client.getCredit(email));
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void InvalidStationID() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina(stationIDInvalid, email);	
		
		assertEquals(9, client.getCredit(email));
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void InvalidStationNull() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina(null, email);	
		
		assertEquals(9, client.getCredit(email));
	}
	
	@Test (expected = InvalidStation_Exception.class)
	public void InvalidStationEmptyID() throws BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.rentBina(stationID1, email);
		client.returnBina("", email);	
		
		assertEquals(9, client.getCredit(email));
	}
	
	@Test (expected = UserNotExists_Exception.class)
	//Nao preciso do rent bina porque chega primeiro ao caso que verifica o user 
	public void UserNotRegisteredD() throws BadInit_Exception, InvalidStation_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
		
		client.returnBina(stationID1, emailNotActive);	
		
		assertEquals(10, client.getCredit(email));
		
	}
	
	@Test (expected = UserNotExists_Exception.class)
	//Nao preciso do rent bina porque chega primeiro ao caso que verifica o user 
	public void UserNull() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
	
		client.returnBina(stationID1, null);	
		
		assertEquals(10, client.getCredit(email));
		assertEquals(20, client.getInfoStation(stationID1).getAvailableBinas());
	}
	
	@Test (expected = UserNotExists_Exception.class)
	//Nao preciso do rent bina porque chega primeiro ao caso que verifica o user 
	public void UserEmpty() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception{
		client.testInitStation(stationID1, 5, 5, 20, 10);
	
		client.returnBina(stationID1, "");	
		
		assertEquals(10, client.getCredit(email));
		assertEquals(20, client.getInfoStation(stationID1).getAvailableBinas());
	}
	
	@Test	(expected = NoBinaRented_Exception.class)
	public void NoneRented() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception, AlreadyHasBina_Exception{
		client.testInitStation(this.stationID1, 5, 5, 20, 10);
		client.returnBina(stationID1, email);

		assertEquals(20, client.getInfoStation(stationID1).getAvailableBinas());
		assertEquals(10, client.getCredit(email));
	}
	
	@Test	(expected = NoBinaRented_Exception.class)
	public void LreadyReturnedSameStation() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception, AlreadyHasBina_Exception{
		client.testInitStation(this.stationID1, 5, 5, 20, 10);
		client.rentBina(stationID1, email);
		client.returnBina(stationID1, email);

		assertEquals(20, client.getInfoStation(stationID1).getAvailableBinas());
		assertEquals(19, client.getCredit(email));
	}
	
	@Test	(expected = NoBinaRented_Exception.class)
	public void LreadyReturnedDifferentStation() throws BadInit_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, FullStation_Exception, NoBinaRented_Exception, AlreadyHasBina_Exception{
		client.testInitStation(this.stationID1, 5, 5, 20, 10);
		client.rentBina(stationID1, email);
		client.returnBina(stationID2, email);

		assertEquals(20, client.getInfoStation(stationID1).getAvailableBinas());
		assertEquals(19, client.getCredit(email));
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
	
}


