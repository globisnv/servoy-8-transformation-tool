package entities;

import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exceptions.FormTransformerException;

public class Form extends Element {
	
	private Set<FormElement> items; 

	// CONSTRUCTORS
	
	public Form(String jsonString) {
		super(jsonString);
		this.items = new LinkedHashSet<>();
		try {
			JSONObject jsonObj = new JSONObject(jsonString);
			JSONArray jsonArr = jsonObj.getJSONArray("items");
			
			//System.out.println(jsonArr);
			for (int i = 0 ; i < jsonArr.length() ; i++) {
				JSONObject item = jsonArr.getJSONObject(i);
				items.add(new FormElement(item.toString()));
			}
			
		} catch (JSONException e) {
			throw new FormTransformerException(e);
		}
	}
	
	// TOSTRING
	@Override
	public String toString() {
		return super.toString() + "\nForm [items=" + items + "]";
	}

	// GETTERS & SETTERS
	
	public Set<FormElement> getItems() {
		return items;
	}

	// OTHERS
	
	public void addItem(FormElement formElement) {
		this.items.add(formElement);
	}

	/*
	public void parseJson(JSONObject jsonObj) {
		// TODO
		throw new FormTransformerException(new Exception("not yet implemented"));
		
	}*/

	public void transform7to8() {
		
		if (this.isTransformed()) {
			return;
		}
		
	}
	
	
}
