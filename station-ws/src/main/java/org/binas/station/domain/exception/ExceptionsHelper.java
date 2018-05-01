package org.binas.station.domain.exception;

import org.binas.station.ws.BadInit;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.InvalidCredit;
import org.binas.station.ws.InvalidCredit_Exception;
import org.binas.station.ws.InvalidFormatEmail;
import org.binas.station.ws.InvalidFormatEmail_Exception;
import org.binas.station.ws.NoBinaAvail;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.UserDoesNotExists;
import org.binas.station.ws.UserDoesNotExists_Exception;

/**This is just a class to help throw exceptions*/
public final class ExceptionsHelper {

	 /** Helper to throw a new NoBinaAvail exception. */
	public static void throwNoBinaAvail(final String message) throws
		NoBinaAvail_Exception {
		NoBinaAvail faultInfo = new NoBinaAvail();
		faultInfo.setMessage(message);
		throw new NoBinaAvail_Exception(message, faultInfo);
	}
	
	 /** Helper to throw a new NoSlotAvail exception. */
	public static void throwNoSlotAvail(final String message) throws
		NoSlotAvail_Exception {
		NoSlotAvail faultInfo = new NoSlotAvail();
		faultInfo.setMessage(message);
		throw new NoSlotAvail_Exception(message, faultInfo);
	}
	
	 /** Helper to throw a new BadInit exception. */
	public static void throwBadInit(final String message) throws BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.setMessage(message);
		throw new BadInit_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new InvalidCredit exception. */
	public static void throwInvalidCredit(final String message) throws InvalidCredit_Exception {
		InvalidCredit faultInfo = new InvalidCredit();
		faultInfo.setMessage(message);
		throw new InvalidCredit_Exception(message, faultInfo);
	}
	
	 /** Helper to throw a new InvalidFormatEmail exception. */
	public static void throwInvalidFormatEmail(final String message) throws InvalidFormatEmail_Exception {
		InvalidFormatEmail faultInfo = new InvalidFormatEmail();
		faultInfo.setMessage(message);
		throw new InvalidFormatEmail_Exception(message, faultInfo);
	}
	
	 /** Helper to throw a new UserDoesNotExists exception. */
	public static void throwUserDoesNotExists(final String message) throws UserDoesNotExists_Exception {
		UserDoesNotExists faultInfo = new UserDoesNotExists();
		faultInfo.setMessage(message);
		throw new UserDoesNotExists_Exception(message, faultInfo);
	}
	
}
