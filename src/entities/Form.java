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

				String oldLabelName = "";
				switch (oldFe.formElementIdentifier()) {
				//
				case ElementTypeID.INPUT_TEXTFIELD:
					// TODO : ifLabelExists method algemeen ?
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TEXTFIELD_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.INPUT_CHECKBOX:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_CHECKBOX_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.INPUT_COMBOBOX:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_COMBOBOX_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.INPUT_CALENDAR:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_DATEPICKER_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.INPUT_TEXTAREA:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TEXTAREA_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.INPUT_TYPEAHEAD:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TYPEAHEAD_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.INPUT_RADIO:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_RADIO_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.INPUT_PASSWORD:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_PASSWORD_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.BUTTON:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_BUTTON_TYPENAME, oldLabelName));
					break;
				//
				case ElementTypeID.LABEL:
					break;
				//
				default:
					if (!oldFe.isTransformed()) {
						oldFe.setTransformedTrue();
						newForm.items.add(FormElement.deepCopySyncedTransform(oldFe, false));
					}
					break;
				}

			}
			this.setTransformedTrue();
			return newForm;
		} catch (FormTransformerException e) {
			throw new FormTransformerException(e);
		}
	}

	private String ifLabelExistsSetTransformedTrue(String formElementName) {

		for (FormElement item : this.items) {
			// TODO : als identifier LABEL != BUTTON : herschrijven
			if (item.typeid == ElementTypeID.LABEL) {
				if (item.otherProperties.containsKey("labelFor")
						&& item.otherProperties.get("labelFor").equals(formElementName)) {
					item.setTransformedTrue();
					return item.name;
				}
			}
		}
		return "";
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
