package enums;

public class ElementTypeID {

	// SERVOY 7
	public final static int FORM = 3;
	public final static int INPUT_GENERAL = 4;
	public final static int LABEL = 7;
	public final static int TAB_PANEL = 16;
	public final static int TAB = 15;
	
	// FormElement.formElementIdentifier() returns typeid * -displayType if typeid = 4
	public final static int INPUT_TEXTFIELD = 4;
	public final static int INPUT_TEXTAREA = -4;
	public final static int INPUT_COMBOBOX = -8;
	public final static int INPUT_RADIO = -12;
	public final static int INPUT_CHECKBOX = -16;
	public final static int INPUT_CALENDAR = -20;
	public final static int INPUT_PASSWORD = -24;
	public final static int INPUT_TYPEAHEAD = -40;
	public final static int BUTTON = 0;
	public final static int BTN_SELECT = 999;

	// SERVOY 8
	public final static int MD_INPUT = 47;
	public final static String MD_INPUT_TEXTFIELD_TYPENAME = "svy-md-Input";
	public final static String MD_INPUT_TEXTAREA_TYPENAME = "svy-md-textarea";
	public final static String MD_INPUT_CHECKBOX_TYPENAME = "svy-md-Checkbox";
	public final static String MD_INPUT_COMBOBOX_TYPENAME = "svy-md-Combobox";
	public final static String MD_INPUT_DATEPICKER_TYPENAME = "svy-md-Datepicker";
	public final static String MD_INPUT_TYPEAHEAD_TYPENAME = "svy-md-Autocomplete";
	public final static String MD_INPUT_RADIO_TYPENAME = "svy-md-Radio";
	public final static String MD_INPUT_PASSWORD_TYPENAME = "svy-md-Password";
	public final static String MD_BUTTON_TYPENAME = "svy-md-button";
	public final static String MD_TABPANEL_TYPENAME = "svy-md-Tabpanel";
	public final static String MD_LOOKUPFIELD_TYPENAME = "svy-md-Lookupfield";
	
}
