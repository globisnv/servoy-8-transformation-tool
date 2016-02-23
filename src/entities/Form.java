package entities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import enums.ElementDatatype;
import enums.ElementTypeID;
import enums.FormView;
import exceptions.FormTransformerException;
import main.FormTransformer;

public class Form extends Element {

	private String jsFile = null;
	private final String path;

	// CONSTRUCTORS

	protected Form(String name, int typeid, String path) {
		super(name, typeid);
		this.path = path;
	}

	public Form(String jsonString, String path) {
		super(jsonString);
		this.path = path;

	}

	// TOSTRING
	@Override
	public String toString() {
		return super.toString() + "\nPath = " + path + "\nForm [items=" + items + "]";
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

	public String getUUID() {
		return super.uuid;
	}

	public int getTypeId() {
		return super.typeid;
	}

	public int getView() {
		if (otherProperties.containsKey("view")) {
			return Integer.valueOf(otherProperties.get("view"));
		} else {
			return -1;
		}
	}

	// OTHERS

	public String getPath() {
		return path;
	}

	public static FormElement findParentFormElement(String uuid, Set<Form> forms) {
		for (Form form : forms) {
			for (FormElement item : form.items) {
				if (item.typeid == ElementTypeID.TAB_PANEL) {
					String key = "containsFormID";
					Set<FormElement> itemItems = item.items;
					for (FormElement itemItem : itemItems) {
						if ((itemItem.otherProperties.containsKey(key))
								&& (itemItem.otherProperties.get(key).equals(uuid))) {
							// item = tabPanel
							return item;
						}

					}
				}
			}
		}

		return null;
	}

	public Form transform7dtl() throws FormTransformerException {

		if (this.isTransformed()) {
			return null;
		}

		Form newForm = new Form(FormTransformer.NG_PREFIX + this.name, ElementTypeID.FORM, this.path);
		newForm.otherProperties = this.otherProperties;
		newForm.jsFile = this.jsFile;

		int modifications = 0;

		try {
			for (FormElement oldFe : this.items) {

				String oldLabelText = "";
				switch (oldFe.formElementIdentifier()) {
				//
				case ElementTypeID.INPUT_TEXTFIELD:
					// TODO : ifLabelExists method algemeen ?
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_INPUT_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TEXTFIELD_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_CHECKBOX:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_BUTTON_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_CHECKBOX_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_COMBOBOX:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_INPUT_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_COMBOBOX_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_CALENDAR:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_INPUT_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_DATEPICKER_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_TEXTAREA:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TEXTAREA_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_TYPEAHEAD:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_INPUT_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TYPEAHEAD_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_RADIO:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_BUTTON_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_RADIO_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_PASSWORD:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_INPUT_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_PASSWORD_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.BUTTON:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_BUTTON_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_BUTTON_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.BTN_SELECT:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.setSizeHeightIfSmaller(FormTransformer.DEFAULT_INPUT_HEIGHT);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_LOOKUPFIELD_TYPENAME, oldLabelText));
					modifications++;
					break;
				//
				case ElementTypeID.TAB_PANEL:
					newForm.items.add(oldFe.transform(ElementTypeID.MD_TABPANEL_TYPENAME,
							FormTransformer.NG_PREFIX + oldFe.name));
					modifications++;
					break;
				//

				default:
					if (!oldFe.isTransformed()) {
						oldFe.setTransformedTrue();
						// no modifications ; form element is added to new form
						// as is;
						newForm.items.add(FormElement.deepCopySyncedTransform(oldFe, false));
					}
					break;
				}

			}
			// transform did nothing :
			if (modifications == 0) {
				return this;
			}
			// else :
			this.setTransformedTrue();
			return newForm;
		} catch (FormTransformerException e) {
			throw new FormTransformerException(e);
		}
	}

	public Form transform7lst(FormElement parentFe) throws FormTransformerException {

		if (this.isTransformed()) {
			return null;
		}

		Form newForm = new Form(FormTransformer.NG_PREFIX + this.name, ElementTypeID.FORM, this.path);
		newForm.otherProperties = this.otherProperties;
		newForm.otherProperties.put("view", String.valueOf(FormView.RECORD_VIEW));
		newForm.jsFile = this.jsFile;

		int modifications = 0;

		XYinteger newFormSize = new XYinteger(newForm.otherProperties.get("size"));
		XYinteger parentSize = new XYinteger("100,100"); /* default */
		if (parentFe != null && parentFe.otherProperties.containsKey("size")) {
			parentSize = new XYinteger(parentFe.otherProperties.get("size"));
		} 

		// parentSize > newFormSize ?
		if (parentSize.getX() > newFormSize.getX()) {
			newFormSize.setX(parentSize.getX());
			modifications++;
		}
		if (parentSize.getY() > newFormSize.getY()) {
			newFormSize.setY(parentSize.getY());
			modifications++;
		}
		
		try {
			for (FormElement oldFe : this.items) {

				String oldLabelText = "";
				switch (oldFe.formElementIdentifier()) {
				//
				case ElementTypeID.BODY:
					try {
						int bodyHeight = Integer.valueOf(oldFe.otherProperties.get("height"));
						// bodyHeight < newFormHeight ?
						if (bodyHeight < newFormSize.getY()) {
							oldFe.otherProperties.put("height", String.valueOf(newFormSize.getY()));
							modifications++;
						}
					} catch (NumberFormatException ex) {/* do nothing */
					} catch (FormTransformerException ex) {
						/* do nothing */}

					if (!oldFe.isTransformed()) {
						modifications++;
						oldFe.setTransformedTrue();
						// no modifications ; form element is added to new form
						// as is;
						newForm.items.add(FormElement.deepCopySyncedTransform(oldFe, false));
					}

					break;
				//

				case ElementTypeID.INPUT_CHECKBOX:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.otherProperties.put("cellTemplate", "uiGridview-checkbox.html");
					newForm.items.add(oldFe.transform(ElementTypeID.UI_GRIDVIEW_TEMP_TYPENAME, oldLabelText));
					modifications++;
					break;
				case ElementTypeID.INPUT_COMBOBOX:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.otherProperties.put("cellTemplate", "uiGridview-combobox.html");
					newForm.items.add(oldFe.transform(ElementTypeID.UI_GRIDVIEW_TEMP_TYPENAME, oldLabelText));
					modifications++;
					break;
				case ElementTypeID.INPUT_CALENDAR:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					oldFe.otherProperties.put("cellTemplate", "uiGridview-calendar.html");
					newForm.items.add(oldFe.transform(ElementTypeID.UI_GRIDVIEW_TEMP_TYPENAME, oldLabelText));
					modifications++;
					break;
				case ElementTypeID.BTN_SELECT:
				case ElementTypeID.INPUT_TEXTAREA:
				case ElementTypeID.INPUT_TYPEAHEAD:
				case ElementTypeID.INPUT_TEXTFIELD:
					oldLabelText = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.UI_GRIDVIEW_TEMP_TYPENAME, oldLabelText));
					modifications++;
					break;
				case ElementTypeID.LABEL:
				case ElementTypeID.INPUT_RADIO:
				case ElementTypeID.INPUT_PASSWORD:
				case ElementTypeID.BUTTON:
				case ElementTypeID.TAB_PANEL:
				default:
					// other form elements are excluded
					oldFe.setTransformedTrue();
					break;
				}

			}
			// transform did nothing :
			if (modifications == 0) {
				return this;
			}
			// else :
			this.setTransformedTrue();

			// CREATE ONE GRIDVIEW OUT OF ALL TEMP GRIDVIEWS
			FormElement gridviewFe = new FormElement("gridview", ElementTypeID.MD_INPUT);
			gridviewFe.otherProperties.put("typeName", ElementTypeID.UI_GRIDVIEW_TYPENAME);
			gridviewFe.otherProperties.put("location", "2, 2");
			gridviewFe.otherProperties.put("anchors", "15");
			XYinteger gridviewSize = new XYinteger(newFormSize.toString());
			gridviewSize.setX(gridviewSize.getX() - 4);
			gridviewSize.setY(gridviewSize.getY() - 4);
			gridviewFe.otherProperties.put("size", gridviewSize.toString());
			Map<Integer, Map<String, String>> displayFoundsetHeaders = new TreeMap<>();
			Map<String, String> fsDataproviders = new LinkedHashMap<>();
			String ngFoundset = "";

			int i = 0;
			for (FormElement item : newForm.items) {
				if (!item.isTransformed() && item.typeid == ElementTypeID.UI_GRIDVIEW_TEMP) {
					Map<String, String> displayFoundsetHeader = new LinkedHashMap<>();
					// headerTitle
					if (item.jsonItems.containsKey("label") && item.jsonItems.get("label").length() > 0) {
						displayFoundsetHeader.put("headerTitle", item.jsonItems.get("label"));
					} else {
						if (item.jsonItems.containsKey("titleText")) {
							displayFoundsetHeader.put("headerTitle", item.jsonItems.get("titleText"));
						} else {
							displayFoundsetHeader.put("headerTitle", item.name);
						}
					}
					// columnWidth
					if (item.otherProperties.containsKey("size")) {
						XYinteger size = new XYinteger(item.otherProperties.get("size"));
						displayFoundsetHeader.put("columnWidth", String.valueOf(size.getX()));
					}
					// dataProviderID
					if (item.jsonItems.containsKey("dataProviderID")) {
						displayFoundsetHeader.put("dataProviderID", item.jsonItems.get("dataProviderID"));
					}
					// toolTipText
					if (item.jsonItems.containsKey("toolTipText")) {
						displayFoundsetHeader.put("toolTipText", item.jsonItems.get("toolTipText"));
					}
					// format
					if (item.jsonItems.containsKey("format")) {
						displayFoundsetHeader.put("format", item.jsonItems.get("format"));
					}
					// horizontalAlignment
					if (item.jsonItems.containsKey("horizontalAlignment")) {
						displayFoundsetHeader.put("horizontalAlignment", item.jsonItems.get("horizontalAlignment"));
					}
					// valuelistID
					if (item.jsonItems.containsKey("valuelistID")) {
						displayFoundsetHeader.put("valuelistID", item.jsonItems.get("valuelistID"));
					}
					// cellTemplate
					if (item.jsonItems.containsKey("cellTemplate")) {
						displayFoundsetHeader.put("cellTemplate", item.jsonItems.get("cellTemplate"));
					}
					// position of column
					int position = 0;
					if (item.otherProperties.containsKey("location")) {
						XYinteger xyPos = new XYinteger(item.otherProperties.get("location"));
						position = xyPos.getX();
					} 
					while (displayFoundsetHeaders.containsKey(position)) {
						position++;
					}
					displayFoundsetHeader.put("dpXfromFS", "dp" + i);
					displayFoundsetHeaders.put(position, displayFoundsetHeader);
					fsDataproviders.put("dp" + i, item.name);
					i++;
					item.setTransformedTrue();
					// System.out.println("\nitem : \n"+item);
					// System.out.println("jsonItem : \n"+item.jsonItems);
				}
			}

			// ngFoundset
			StringBuilder builder = new StringBuilder();
			builder.append("{").append(FormTransformer.CRLF).append("foundsetSelector: ").append(FormTransformer.QM)
					.append(ngFoundset).append(FormTransformer.QM).append(",").append(FormTransformer.CRLF)
					.append("dataproviders: ").append(FormTransformer.CRLF).append("{").append(FormTransformer.CRLF);
			for (Entry<String, String> entry : fsDataproviders.entrySet()) {
				builder.append(entry.getKey()).append(": ").append(FormTransformer.QM).append(entry.getValue())
						.append(FormTransformer.QM).append(",").append(FormTransformer.CRLF);
			}
			if (fsDataproviders.size() > 0) {
				builder.setLength(builder.length() - 3);
			}
			builder.append(FormTransformer.CRLF).append("}").append(FormTransformer.CRLF).append(FormTransformer.CRLF)
					.append("}").append(FormTransformer.CRLF);
			gridviewFe.jsonItems.put("ngFoundset", builder.toString());

			// displayFoundsetHeaders
			builder = new StringBuilder();
			builder.append("[").append(FormTransformer.CRLF);

			for (Map<String, String> displayFoundsetHeader : displayFoundsetHeaders.values()) {
				builder.append("{").append(FormTransformer.CRLF);
				for (Entry<String, String> entry : displayFoundsetHeader.entrySet()) {
					builder.append(entry.getKey()).append(": ");
					if (Element.elementKeyValueDatatypes.get(entry.getKey()) == ElementDatatype.STRING) {
						builder.append(FormTransformer.QM).append(entry.getValue()).append(FormTransformer.QM);

					} else {
						builder.append(entry.getValue());
					}
					builder.append(",").append(FormTransformer.CRLF);
				}
				if (displayFoundsetHeader.size() > 0) {
					builder.setLength(builder.length() - 3);
				}
				builder.append(FormTransformer.CRLF).append("},").append(FormTransformer.CRLF);
			}
			if (displayFoundsetHeaders.size() > 0) {
				builder.setLength(builder.length() - 3);
			}
			builder.append(FormTransformer.CRLF).append("]").append(FormTransformer.CRLF);
			gridviewFe.jsonItems.put("displayFoundsetHeaders", builder.toString());

			newForm.items.add(gridviewFe);

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
					return item.otherProperties.get("text");
				}
			}
		}
		return "";
	}

	@Override
	public String toServoyForm() {
		return super.toServoyForm();

	}

}
