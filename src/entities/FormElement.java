package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import enums.ElementDatatype;
import enums.ElementTypeID;
import exceptions.FormTransformerException;

public class FormElement extends Element {

	protected Map<String, String> jsonItems = new HashMap<>();

	// CONSTRUCTORS

	protected FormElement(String jsonString) {
		super(jsonString);

	}

	protected FormElement(String name, int typeid) {
		super(name, typeid);
	}

	private FormElement(FormElement fe, boolean transformedValue) {
		super(fe, transformedValue);
		this.jsonItems = fe.jsonItems;
	}

	// GETTERS & SETTERS

	// OTHERS

	@Override
	public String toServoyForm() {
		StringBuilder builder = new StringBuilder(super.toServoyForm());

		if (this.jsonItems.size() > 0) {

			builder.append("," + CRLF + "json: {" + CRLF);
			int builderLengthNoJsonItems = builder.length();

			for (Entry<String, String> jsonItem : this.jsonItems.entrySet()) {
				String itemValue = jsonItem.getValue();
				if (Element.getElementKeyValueDatatype(jsonItem.getKey()) == ElementDatatype.STRING) {
					itemValue = QM + itemValue + QM;
				}
				builder.append(jsonItem.getKey()).append(": ").append(itemValue).append(",").append(CRLF);
			}
			if (builder.lastIndexOf(",") > builderLengthNoJsonItems) {
				builder.setLength(builder.length() - 3);
			}
			builder.append("}" + CRLF);
		}
		return builder.toString();
	}

	protected static void moveFromOtherProperties(Map<String, String> source, Map<String, String> destination,
			String key) throws FormTransformerException {
		if (source.containsKey(key)) {
			destination.put(key, source.get(key));
			source.remove(key);
		}
		return;
	}

	protected int formElementIdentifier() {
		// problem : multiple form elements have typeid = 4 ;
		// distinguished by multiplying with -displayType
		int negComponentToIdentifyDiffInputs = 1;
		try {
			if (this.typeid == ElementTypeID.INPUT_GENERAL && this.otherProperties.containsKey("displayType")) {
				negComponentToIdentifyDiffInputs = -1 * Integer.valueOf(this.otherProperties.get("displayType"));
			}
		} catch (NumberFormatException e) {
		}
		return negComponentToIdentifyDiffInputs * this.typeid;
	}

	protected static FormElement deepCopySyncedTransform(FormElement oldFe, boolean transformedValue) {
		// if element is created out of another,
		// and the "transformed" state of one of them is set as TRUE,
		// then both "transformed" states have to be set as TRUE
		FormElement newFe = new FormElement(oldFe, transformedValue);
		oldFe.duplicateOfElement = newFe;
		return newFe;
	}

}
