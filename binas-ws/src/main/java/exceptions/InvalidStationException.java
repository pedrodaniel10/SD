package exceptions;

/** Exception used to signal that the station name doesn't exist. */
public class InvalidStationException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidStationException() {
	}

	public InvalidStationException(String message) {
		super(message);
	}
}
