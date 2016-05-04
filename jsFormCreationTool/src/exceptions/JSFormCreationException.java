package exceptions;

public class JSFormCreationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public JSFormCreationException(Exception e) {
		super(e);
	}
	
	public JSFormCreationException(String msg) {
		super(msg);
	}

}
