package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import enums.ElementDatatype;
import enums.ElementTypeID;
import exceptions.FormTransformerException;
import main.FormTransformer;

public class FormElement extends Element {

	protected Map<String, String> jsonItems = new HashMap<>();

	// CONSTRUCTORS

	protected FormElement(String jsonString) {
		super(jsonString);

	}

	private FormElement(String name, int typeid) {
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

	private static void moveFromOtherProperties(Map<String, String> source, Map<String, String> destination,
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
			if (this.typeid == ElementTypeID.LABEL && this.otherProperties.containsKey("labelFor")) {
				return ElementTypeID.LABEL;
			}
			if (this.typeid == ElementTypeID.LABEL && this.otherProperties.containsKey("onDoubleClickMethodID")) {
				// has no "labelFor" & has onDoubleClickMethodID : considered BUTTON
				return ElementTypeID.BUTTON;
			}
			// LABELS without "labelFor" OR "onDoubleClickMethodID" are considered LABELS
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

	protected FormElement transform(String mdComponentIdentifier, String oldLabelName)
			throws FormTransformerException {

		FormElement newFe;

		switch (mdComponentIdentifier) {
		case ElementTypeID.MD_INPUT_TEXTFIELD_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_TEXTFIELD_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_CHECKBOX_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_CHECKBOX_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_COMBOBOX_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_COMBOBOX_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_DATEPICKER_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_DATEPICKER_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_TEXTAREA_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_TEXTAREA_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_TYPEAHEAD_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_TYPEAHEAD_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_RADIO_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_RADIO_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_PASSWORD_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_PASSWORD_TYPENAME);
			break;
		case ElementTypeID.MD_BUTTON_TYPENAME:
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_BUTTON_TYPENAME);
			break;
		case ElementTypeID.MD_TABPANEL_TYPENAME:
			System.out.println("*** TAB =\n"+this);
			newFe = new FormElement(FormTransformer.NG_PREFIX + this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_TABPANEL_TYPENAME);
			break;
		default:
			throw new FormTransformerException("Not a valid mdComponentIdentifier ["+mdComponentIdentifier+"] !");
		}

		// other props - if present
		FormElement.moveFromOtherProperties(this.otherProperties, newFe.otherProperties, "location");
		FormElement.moveFromOtherProperties(this.otherProperties, newFe.otherProperties, "size");
		FormElement.moveFromOtherProperties(this.otherProperties, newFe.otherProperties, "anchor");
		// jsonItems create label + copy remaining other props
		newFe.jsonItems.put("label", oldLabelName);
		newFe.jsonItems.putAll(this.otherProperties);
		
		this.setTransformedTrue();
		return newFe;
	}

}
