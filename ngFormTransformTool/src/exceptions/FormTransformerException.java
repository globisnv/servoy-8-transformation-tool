package exceptions;

public class FormTransformerException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public FormTransformerException(Exception e) {
		super(e);
	}
	
	public FormTransformerException(String msg) {
		super(msg);
	}

}
