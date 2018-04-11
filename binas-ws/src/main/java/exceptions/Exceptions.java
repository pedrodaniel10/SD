package exceptions;

import org.binas.ws.EmailExists;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail;
import org.binas.ws.InvalidEmail_Exception;
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
}
