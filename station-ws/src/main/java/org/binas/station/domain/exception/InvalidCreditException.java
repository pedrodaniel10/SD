package org.binas.station.domain.exception;

/** Exception used to signal a problem while setting a balance. */
public class InvalidCreditException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidCreditException() {
	}

	public InvalidCreditException(String message) {
		super(message);
	}
}
