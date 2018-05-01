package exceptions;

/** Exception used to signal that the email is already registered. */
public class EmailExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	public EmailExistsException() {
	}

	public EmailExistsException(String message) {
		super(message);
	}
}
