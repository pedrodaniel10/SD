package exceptions;

/** Exception used to signal that the user doesn'n have credit. */
public class NoCreditException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoCreditException() {
	}

	public NoCreditException(String message) {
		super(message);
	}
}
