package entities;

import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONObject;

import exceptions.FormTransformerException;

public class Form extends Element {
	
	private Set<FormElement> items; 

	// CONSTRUCTORS
	
	public Form(String jsonString) {
		super(jsonString);
		this.items = new LinkedHashSet<>();
	}

	// GETTERS & SETTERS
	
	public Set<FormElement> getItems() {
		return items;
	}

	// OTHERS
	
	public void addItem(FormElement formElement) {
		this.items.add(formElement);
	}

	@Override
	public void parseJson(JSONObject jsonObj) {
		// TODO
		throw new FormTransformerException(new Exception("not yet implemented"));
		
	}

	
	
}
