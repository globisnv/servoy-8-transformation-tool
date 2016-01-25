package entities;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import enums.ElementDatatype;
import enums.ElementTypeID;
import exceptions.FormTransformerException;

public class Form extends Element {

	private Set<FormElement> items;

	// CONSTRUCTORS

	public Form(String uuid, String name, int typeid) {
		super(uuid, name, typeid);
		this.items = new LinkedHashSet<>();
	}

	public Form(String jsonString) {
		super(jsonString);
		this.items = new LinkedHashSet<>();
		try {
			JSONObject jsonObj = new JSONObject(jsonString);
			JSONArray jsonArr = jsonObj.getJSONArray("items");

			// System.out.println(jsonArr);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject item = jsonArr.getJSONObject(i);
				items.add(new FormElement(item.toString()));
			}

		} catch (JSONException e) {
			System.out.println("Form/jsonString = "+jsonString);
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
	 * public void parseJson(JSONObject jsonObj) { // TODO throw new
	 * FormTransformerException(new Exception("not yet implemented"));
	 * 
	 * }
	 */

	public Form transform7to8() {

		if (this.isTransformed()) {
			return null;
		}

		Form newForm = new Form(UUID.randomUUID().toString(), "ng$" + this.getName(), ElementTypeID.FORM);
		newForm.setOtherProperties(this.getOtherProperties());

		for (FormElement item : this.items) {
			switch (item.getTypeid()) {
			case ElementTypeID.INPUT:
				FormElement oldLabel = getLabelForName(item.getName());
				FormElement newItem = new FormElement(UUID.randomUUID().toString(), this.getName(),
						ElementTypeID.MD_INPUT);
				newItem.addOtherProperty("typeName", ElementTypeID.MD_INPUT_Name);

				newForm.addItem(newItem);
				item.setTransformed(true);
				oldLabel.setTransformed(true);
				break;
			default:
				break;
			}

		}
		this.setTransformed(true);
		return newForm;
	}

	private FormElement getLabelForName(String formElementName) {

		for (FormElement item : this.items) {
			if (item.getTypeid() == ElementTypeID.LABEL) {
				if (item.getOtherProperties().containsKey("labelFor")
						&& item.getOtherProperties().get("labelFor").equals(formElementName)) {
					return item;
				}
			}
		}
		FormElement noLabelFound = new FormElement(UUID.randomUUID().toString(), "", ElementTypeID.LABEL);
		noLabelFound.setTransformed(true);
		return noLabelFound;
	}
	
	@Override
	public String toServoyForm() {
		
		StringBuilder builder = new StringBuilder(super.toServoyForm());
		builder.append("," + CRLF);
		builder.append("items: [" + CRLF);
		
		for (FormElement item : this.getItems()) {
			builder.append(item.toServoyForm());
		}
		builder.append("]" + CRLF);
		return builder.toString();
	}
	
	
	

}
