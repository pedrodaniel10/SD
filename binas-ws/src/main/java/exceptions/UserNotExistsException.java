package exceptions;

/** Exception used to signal that the user doesn't exist. */
public class UserNotExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	public UserNotExistsException() {
	}

	public UserNotExistsException(String message) {
		super(message);
	}
}
