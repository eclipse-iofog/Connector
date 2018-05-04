package main.java.org.eclipse.iofog.comsat.exceptions;

public class DuplicateIdException extends Exception {
	private static final long serialVersionUID = -5075882609298125930L;

	public DuplicateIdException(String message) {
		super(message);
	}
	
	public DuplicateIdException() {
		super();
	}
}
