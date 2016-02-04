package entities;

import enums.ElementTypeID;
import exceptions.FormTransformerException;
import main.FormTransformer;

public class Form extends Element {

	//private Set<FormElement> items;
	private String jsFile = null;
	private final String path;

	// CONSTRUCTORS

	protected Form(String name, int typeid, String path) {
		super(name, typeid);
		//this.items = new LinkedHashSet<>();
		this.path = path;
	}

	public Form(String jsonString, String path) {
		super(jsonString);
		this.path = path;
		
	}

	// TOSTRING
	@Override
	public String toString() {
		return super.toString() + "\nPath = "+path+"\nForm [items=" + items + "]";
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

	// OTHERS

	public String getPath() {
		return path;
	}

	public Form transform7to8() throws FormTransformerException {

		if (this.isTransformed()) {
			return null;
		}

		Form newForm = new Form(FormTransformer.NG_PREFIX + this.name, ElementTypeID.FORM, this.path);
		newForm.otherProperties = this.otherProperties;
		newForm.jsFile = this.jsFile;
		
		int modifications = 0;
		
		try {
			for (FormElement oldFe : this.items) {

				String oldLabelName = "";
				switch (oldFe.formElementIdentifier()) {
				//
				case ElementTypeID.INPUT_TEXTFIELD:
					// TODO : ifLabelExists method algemeen ?
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TEXTFIELD_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_CHECKBOX:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_CHECKBOX_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_COMBOBOX:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_COMBOBOX_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_CALENDAR:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_DATEPICKER_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_TEXTAREA:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TEXTAREA_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_TYPEAHEAD:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_TYPEAHEAD_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_RADIO:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_RADIO_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.INPUT_PASSWORD:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_INPUT_PASSWORD_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.BUTTON:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_BUTTON_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.BTN_SELECT:
					oldLabelName = ifLabelExistsSetTransformedTrue(oldFe.name);
					newForm.items.add(oldFe.transform(ElementTypeID.MD_LOOKUPFIELD_TYPENAME, oldLabelName));
					modifications++;
					break;
				//
				case ElementTypeID.TAB_PANEL:
					newForm.items.add(oldFe.transform(ElementTypeID.MD_TABPANEL_TYPENAME, FormTransformer.NG_PREFIX+oldFe.name));
					modifications++;
					break;
				//
				
				default:
					if (!oldFe.isTransformed()) {
						oldFe.setTransformedTrue();
						// no modifications ; form element is added to new form as is;
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
		return super.toServoyForm();
		
	}

}
