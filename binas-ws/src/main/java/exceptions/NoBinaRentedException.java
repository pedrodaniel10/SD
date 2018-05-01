package exceptions;

/** Exception used to signal that the user doesn't have a bina rented. */
public class NoBinaRentedException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoBinaRentedException() {
	}

	public NoBinaRentedException(String message) {
		super(message);
	}
}
