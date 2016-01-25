package entities;

import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import enums.ElementTypeID;
import exceptions.FormTransformerException;

public class Form extends Element {

	private Set<FormElement> items;

	// CONSTRUCTORS

	public Form(String name, int typeid) {
		super(name, typeid);
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
			System.out.println("Form/jsonString = " + jsonString);
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

	public Form transform7to8() throws FormTransformerException {

		if (this.isTransformed()) {
			return null;
		}

		Form newForm = new Form("ng$" + this.getName(), ElementTypeID.FORM);
		newForm.setOtherProperties(this.getOtherProperties());
		try {
			for (FormElement item : this.items) {
				switch (item.getTypeid()) {
				//
				case ElementTypeID.INPUT:
					FormElement oldLabel = findLabelForName(item.getName());

					FormElement newItem = new FormElement(this.getName(), ElementTypeID.MD_INPUT);
					newItem.addOtherProperty("typeName", ElementTypeID.MD_INPUT_Name);
					newItem.addOtherProperty("location", findInOtherProperties(item, "location"));
					newItem.addOtherProperty("size", findInOtherProperties(item, "size"));
					StringBuilder builder = new StringBuilder();
					builder.append("{");
					builder.append("dataProviderID: " + QM).append(findInOtherProperties(item, "dataProviderID"))
							.append(QM + "," + CRLF);
					builder.append("label: " + QM).append(oldLabel.getName()).append(QM + CRLF);
					builder.append("}" + CRLF);
					newItem.addOtherProperty("json", builder.toString());
					newForm.addItem(newItem);
					item.setTransformed(true);
					oldLabel.setTransformed(true);
					break;
				//
				default:
					newForm.addItem(item);
					break;
				}

			}
			this.setTransformed(true);
			return newForm;
		} catch (FormTransformerException e) {
			throw new FormTransformerException(e);
		}
	}

	private FormElement findLabelForName(String formElementName) {

		for (FormElement item : this.items) {
			if (item.getTypeid() == ElementTypeID.LABEL) {
				if (item.getOtherProperties().containsKey("labelFor")
						&& item.getOtherProperties().get("labelFor").equals(formElementName)) {
					return item;
				}
			}
		}
		FormElement noLabelFound = new FormElement("", ElementTypeID.LABEL);
		noLabelFound.setTransformed(true);
		return noLabelFound;
	}

	private String findInOtherProperties(FormElement element, String key) throws FormTransformerException {
		if (element.otherProperties.containsKey(key)) {
			return element.otherProperties.get(key);
		}
		throw new FormTransformerException(key + ": key not found in this.otherProperties !");
	}

	@Override
	public String toServoyForm() {

		StringBuilder builder = new StringBuilder(super.toServoyForm());
		builder.append("," + CRLF);
		builder.append("items: [" + CRLF);
		int builderLengthNoItems = builder.length();

		for (FormElement item : this.getItems()) {
			if (!item.isTransformed()) {
				builder.append("{" + CRLF);
				builder.append(item.toServoyForm());
				builder.append(CRLF + "}").append("," + CRLF);
			}

		}
		if (builder.lastIndexOf(",") > builderLengthNoItems) {
			builder.setLength(builder.length() - 3);
		}
		builder.append(CRLF + "]" + CRLF);
		return builder.toString();
	}

}
