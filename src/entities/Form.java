package entities;

import java.util.LinkedHashSet;
import java.util.Set;

public class Form extends Element {
	
	private Set<FormElement> items; 

	// CONSTRUCTORS
	
	public Form(String uuid, String name, int typeid) {
		super(uuid, name, typeid);
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

	
	
}
