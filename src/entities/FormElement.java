package entities;

import org.json.JSONObject;

import exceptions.FormTransformerException;

public class FormElement extends Element {

	// CONSTRUCTORS
	
	public FormElement(String jsonString) {
		super(jsonString);
		
	}
	
	// GETTERS & SETTERS
	
	// OTHERS
	
	@Override
	public void parseJson(JSONObject jsonObj) {
		// TODO
		throw new FormTransformerException(new Exception("not yet implemented"));
		
	}

}
