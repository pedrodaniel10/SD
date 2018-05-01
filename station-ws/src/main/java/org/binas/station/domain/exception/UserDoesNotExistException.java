package org.binas.station.domain.exception;

/** Exception used to signal a problem while getting a balance. */
public class UserDoesNotExistException extends Exception {
	private static final long serialVersionUID = 1L;

	public UserDoesNotExistException() {
	}

	public UserDoesNotExistException(String message) {
		super(message);
	}
}
