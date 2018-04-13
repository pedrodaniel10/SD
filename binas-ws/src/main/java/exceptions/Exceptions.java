package exceptions;

import org.binas.ws.NoBinaAvail;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.AlreadyHasBina;
import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.FullStation;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidEmail;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaRented;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.UserNotExists;
import org.binas.ws.UserNotExists_Exception;

/**This is just a class to help throw exceptions*/
public final class Exceptions {

	/** Helper to throw a new EmailExists exception. */
	public static void throwEmailExists(final String message) throws
		EmailExists_Exception {
		EmailExists faultInfo = new EmailExists();
		faultInfo.setMessage(message);
		throw new EmailExists_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new InvalidEmail exception. */
	public static void throwInvalidEmail(final String message) throws
		InvalidEmail_Exception {
		InvalidEmail faultInfo = new InvalidEmail();
		faultInfo.setMessage(message);
		throw new InvalidEmail_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new UserNotExists exception. */
	public static void throwUserNotExists(final String message) throws
		UserNotExists_Exception {
		UserNotExists faultInfo = new UserNotExists();
		faultInfo.setMessage(message);
		throw new UserNotExists_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new NoCredit exception. */
	public static void throwNoCredit(final String message) throws
		NoCredit_Exception {
		NoCredit faultInfo = new NoCredit();
		faultInfo.setMessage(message);
		throw new NoCredit_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new BadInit exception. */
	public static void throwBadInit(final String message) throws
		BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.setMessage(message);
		throw new BadInit_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new InvalidStation exception. */
	public static void throwInvalidStation(final String message) throws
		InvalidStation_Exception {
		InvalidStation invalidStation = new InvalidStation();
		invalidStation.setMessage(message);
		throw new InvalidStation_Exception(message, invalidStation);
	}
	
	/** Helper to throw a new FullStation exception. */
	public static void throwFullStation(final String message) throws
		FullStation_Exception {
		FullStation fullStation = new FullStation();
		fullStation.setMessage(message);
		throw new FullStation_Exception(message, fullStation);
	}
	
	/** Helper to throw a new NoBinaRented exception. */
	public static void throwNoBinaRented(final String message) throws
		NoBinaRented_Exception {
		NoBinaRented noBinaRented = new NoBinaRented();
		noBinaRented.setMessage(message);
		throw new NoBinaRented_Exception(message, noBinaRented);
	}
	
	/** Helper to throw a new AlreadyHasBina exception. */
	public static void throwAlreadyHasBina(final String message) throws
		AlreadyHasBina_Exception {
		AlreadyHasBina alreadyHasBina = new AlreadyHasBina();
		alreadyHasBina.setMessage(message);
		throw new AlreadyHasBina_Exception(message, alreadyHasBina);
	}
	
	/** Helper to throw a new NoBinaAvail exception. */
	public static void throwNoBinaAvail(final String message) throws
		NoBinaAvail_Exception {
		NoBinaAvail noBinaAvail = new NoBinaAvail();
		noBinaAvail.setMessage(message);
		throw new NoBinaAvail_Exception(message, noBinaAvail);
	}
	
	
}
