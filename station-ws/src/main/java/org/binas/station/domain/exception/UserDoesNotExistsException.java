package org.binas.station.domain.exception;

/** Exception used to signal a problem while getting a balance. */
public class UserDoesNotExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	public UserDoesNotExistsException() {
	}

	public UserDoesNotExistsException(String message) {
		super(message);
	}
}
