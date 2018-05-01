package exceptions;

/** Exception used to signal that the email is invalid. */
public class InvalidEmailException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidEmailException() {
	}

	public InvalidEmailException(String message) {
		super(message);
	}
}
