package org.binas.station.domain.exception;

/** Exception used to signal a problem while getting a balance. */
public class InvalidFormatEmailException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidFormatEmailException() {
	}

	public InvalidFormatEmailException(String message) {
		super(message);
	}
}
