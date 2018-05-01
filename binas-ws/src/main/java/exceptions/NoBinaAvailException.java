package exceptions;

/** Exception used to signal that there is no bina available. */
public class NoBinaAvailException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoBinaAvailException() {
	}

	public NoBinaAvailException(String message) {
		super(message);
	}
}
