package entities;

public class FormElement extends Element {

	// CONSTRUCTORS
	
	public FormElement(String jsonString) {
		super(jsonString);
		
	}

	public FormElement(String uuid, String name, int typeid) {
		super(uuid, name, typeid);
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
