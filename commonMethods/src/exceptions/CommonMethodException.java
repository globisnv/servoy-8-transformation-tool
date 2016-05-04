package exceptions;

public class CommonMethodException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CommonMethodException(Exception e) {
		super(e);
	}
	
	public CommonMethodException(String msg) {
		super(msg);
	}

}
