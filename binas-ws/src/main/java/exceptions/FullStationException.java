package exceptions;

/** Exception used to signal that the station is full. */
public class FullStationException extends Exception {
	private static final long serialVersionUID = 1L;

	public FullStationException() {
	}

	public FullStationException(String message) {
		super(message);
	}
}
