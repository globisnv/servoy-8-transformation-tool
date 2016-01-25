package entities;

import java.util.HashMap;
import java.util.Map;

public class FormElement extends Element {
	
	private Map<String, String> jsonItem = new HashMap<>();
	morgen verder !

	// CONSTRUCTORS
	
	public FormElement(String jsonString) {
		super(jsonString);
		
	}

	public FormElement(String name, int typeid) {
		super(name, typeid);
	}

	
	// GETTERS & SETTERS
	
	// OTHERS
	/*
	@Override
	public void parseJson(JSONObject jsonObj) {
		// TODO
		throw new FormTransformerException(new Exception("not yet implemented"));
		
	}*/
	
	@Override
	public String toServoyForm() {
		return super.toServoyForm();
	}
	
	
	
	
	
}
