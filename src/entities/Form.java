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
	private String jsFile = null;

	// CONSTRUCTORS

	protected Form(String name, int typeid) {
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

	public String getJsFile() {
		return jsFile;
	}

	public void setJsFile(String jsFile) {
		this.jsFile = jsFile;
	}
	
	public String getName() {
		return this.name;
	}

	// OTHERS

	public Form transform7to8() throws FormTransformerException {

		if (this.isTransformed()) {
			return null;
		}

		Form newForm = new Form("ng$" + this.name, ElementTypeID.FORM);
		newForm.otherProperties = this.otherProperties;
		
		newForm.jsFile = this.jsFile;
		try {
			for (FormElement oldFe : this.items) {

				switch (oldFe.formElementIdentifier()) {
				//
				case ElementTypeID.INPUT_TEXTFIELD:
					FormElement oldLabel = findLabelForName(oldFe.name);

					FormElement newFe = new FormElement("ng$" + oldFe.name, ElementTypeID.MD_INPUT);
					newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_Name);
					// other props - if present
					FormElement.moveFromOtherProperties(oldFe.otherProperties, newFe.otherProperties, "location");
					FormElement.moveFromOtherProperties(oldFe.otherProperties, newFe.otherProperties, "size");
					FormElement.moveFromOtherProperties(oldFe.otherProperties, newFe.otherProperties, "anchor");
					// jsonItems
					FormElement.moveFromOtherProperties(oldFe.otherProperties, newFe.jsonItems, "dataProviderID");
					newFe.jsonItems.put("label", oldLabel.name);
					// copy remaining other props
					newFe.jsonItems.putAll(oldFe.otherProperties);
					// put on form
					newForm.items.add(newFe);
					oldFe.setTransformedAsTrue();
					oldLabel.setTransformedAsTrue();
					//
					break;
				//
				default:
					if (!oldFe.isTransformed()) {
						newForm.items.add(new FormElement(oldFe));
						oldFe.setTransformedAsTrue();
					}
					
					break;
				}

			}
			this.setTransformedAsTrue();
			return newForm;
		} catch (FormTransformerException e) {
			throw new FormTransformerException(e);
		}
	}

	private FormElement findLabelForName(String formElementName) {

		for (FormElement item : this.items) {
			if (item.typeid == ElementTypeID.LABEL) {
				if (item.otherProperties.containsKey("labelFor")
						&& item.otherProperties.get("labelFor").equals(formElementName)) {
					return item;
				}
			}
		}
		FormElement noLabelFound = new FormElement("", ElementTypeID.LABEL);
		noLabelFound.setTransformedAsTrue();
		return noLabelFound;
	}

	@Override
	public String toServoyForm() {

		StringBuilder builder = new StringBuilder(super.toServoyForm());
		builder.append("," + CRLF);
		builder.append("items: [" + CRLF);
		int builderLengthNoItems = builder.length();
		
		for (FormElement item : this.items) {
			
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
