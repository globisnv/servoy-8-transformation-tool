package entities;

import exceptions.FormTransformerException;
import main.FormTransformer;

public class XYinteger {
	
	private int x = 0;
	private int y = 0;
	
	// CONSTRUCTORS
	public XYinteger(String string) throws FormTransformerException {
		super();
		if (string == null) {
			throw new FormTransformerException("string can not be NULL");
		}
		string = string.replaceAll("\"", "");
		string = string.replaceAll(" ", "");
		String[] numbers = string.split(",");
		if (numbers.length != 2) throw new FormTransformerException("Incorrect argument ["+string+"]");
		try {
			this.x = Integer.valueOf(numbers[0]);
			this.y = Integer.valueOf(numbers[1]);
		} catch (NumberFormatException ex) {
			throw new FormTransformerException(ex);
		}
		
		
	}
	
	// GETTERS & SETTERS
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
	
	
	// OTHER

	@Override
	public String toString() {
		return this.x + ", " + this.y;
	}

	
}
