package org.eclipse.iofog.comsat.exceptions;

public class NotFoundException extends Exception {
	private static final long serialVersionUID = -7152467337681593296L;

	public NotFoundException(String message) {
		super(message);
	}
	
	public NotFoundException() {
		super();
	}
}
