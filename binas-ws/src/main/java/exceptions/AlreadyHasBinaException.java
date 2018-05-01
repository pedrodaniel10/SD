package exceptions;

/** Exception used to signal that the user already has a bina. */
public class AlreadyHasBinaException extends Exception {
	private static final long serialVersionUID = 1L;

	public AlreadyHasBinaException() {
	}

	public AlreadyHasBinaException(String message) {
		super(message);
	}
}
