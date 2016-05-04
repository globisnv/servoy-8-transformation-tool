package entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import enums.ElementDatatype;
import enums.ElementTypeID;
import exceptions.FormTransformerException;
import main.FormTransformer;

public class FormElement extends Element {

	protected Map<String, String> jsonItems = new HashMap<>();
	protected Set<Map<String, String>> jsonTabs = new HashSet<>();

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

			builder.append("," + FormTransformer.CRLF + "json: {" + FormTransformer.CRLF);
			
			if (this.jsonTabs.size() > 0) {
				builder.append("tabs: [" + FormTransformer.CRLF);
				for (Map<String, String> jsonTab : this.jsonTabs) {
					builder.append("{");
					for (Entry<String, String> jsonTabItem : jsonTab.entrySet()) {
						builder.append(jsonTabItem.getKey()).append(": ")
						.append(FormTransformer.QM + jsonTabItem.getValue() + FormTransformer.QM).append(", ");
					}
					builder.append("active: true"+FormTransformer.CRLF);
					builder.append("},"+FormTransformer.CRLF);
				}
				builder.setLength(builder.length() - 3);
				builder.append(FormTransformer.CRLF + "]," + FormTransformer.CRLF);
				builder.append(FormTransformer.CRLF + "visible: true," + FormTransformer.CRLF);
			}

			for (Entry<String, String> jsonItem : this.jsonItems.entrySet()) {
				String itemValue = jsonItem.getValue();
				if (Element.getElementKeyValueDatatype(jsonItem.getKey()) == ElementDatatype.STRING) {
					itemValue = FormTransformer.QM + itemValue + FormTransformer.QM;
				}
				builder.append(jsonItem.getKey()).append(": ").append(itemValue).append(",").append(FormTransformer.CRLF);
			}
			if (this.jsonItems.size() > 0) {
				builder.setLength(builder.length() - 3);
			}
			builder.append("}" + FormTransformer.CRLF);
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
			if (this.typeid == ElementTypeID.LABEL && this.otherProperties.containsValue(FormTransformer.SEARCH_ICON_IMAGEMEDIA_ID)) {
				// has no "labelFor" & has the UNIQUE uuid for the search icon
				return ElementTypeID.BTN_SELECT;
			}
			// LABELS non of the above, are considered LABELS
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

	protected FormElement transform(String mdComponentIdentifier, String oldLabelText)
			throws FormTransformerException {

		FormElement newFe;

		switch (mdComponentIdentifier) {
		case ElementTypeID.MD_INPUT_TEXTFIELD_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_TEXTFIELD_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_CHECKBOX_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_CHECKBOX_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_COMBOBOX_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_COMBOBOX_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_DATEPICKER_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_DATEPICKER_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_TEXTAREA_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_TEXTAREA_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_TYPEAHEAD_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_TYPEAHEAD_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_RADIO_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_RADIO_TYPENAME);
			break;
		case ElementTypeID.MD_INPUT_PASSWORD_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_INPUT_PASSWORD_TYPENAME);
			break;
		case ElementTypeID.MD_BUTTON_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_BUTTON_TYPENAME);
			break;
		case ElementTypeID.MD_LOOKUPFIELD_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_LOOKUPFIELD_TYPENAME);
			break;
		case ElementTypeID.MD_TABPANEL_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.MD_INPUT);
			newFe.otherProperties.put("typeName", ElementTypeID.MD_TABPANEL_TYPENAME);
			for (FormElement thisItem : this.items) {
				Map<String, String> jsonTab = new HashMap<>();
				FormElement.moveFromOtherProperties(thisItem.otherProperties, jsonTab, "containsFormID");
				FormElement.moveFromOtherProperties(thisItem.otherProperties, jsonTab, "relationName");
				FormElement.moveFromOtherProperties(thisItem.otherProperties, jsonTab, "text");
				jsonTab.put("name", thisItem.name);
				newFe.jsonTabs.add(jsonTab);
			}
			break;
		case ElementTypeID.UI_GRIDVIEW_TEMP_TYPENAME:
			newFe = new FormElement(this.name, ElementTypeID.UI_GRIDVIEW_TEMP);
			
			break;
		default:
			throw new FormTransformerException("Not a valid mdComponentIdentifier ["+mdComponentIdentifier+"] !");
		}

		// other props - if present
		FormElement.moveFromOtherProperties(this.otherProperties, newFe.otherProperties, "location");
		FormElement.moveFromOtherProperties(this.otherProperties, newFe.otherProperties, "size");
		FormElement.moveFromOtherProperties(this.otherProperties, newFe.otherProperties, "anchors");
		// jsonItems create label + copy remaining other props
		// TODO : label enkel als nodig => input, password, ...
		newFe.jsonItems.put("label", oldLabelText);
		newFe.jsonItems.putAll(this.otherProperties);
		
		this.setTransformedTrue();
		return newFe;
	}
	
	protected void setSizeHeightIfSmaller(int height) {
		if (this.otherProperties.containsKey("size")) {
			XYinteger size = new XYinteger(this.otherProperties.get("size"));
			if (size.getY() < height) {
				size.setY(height);
				this.otherProperties.put("size", size.toString());
			}
		}
	}

}
