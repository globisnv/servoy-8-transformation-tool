package enums;

public class ElementTypeID {

	// SERVOY 7
	public final static int FORM = 3;
	public final static int INPUT_GENERAL = 4;
	public final static int LABEL = 7;
	
	// FormElement.formElementIdentifier() returns typeid * -displayType if typeid = 4
	public final static int INPUT_TEXTFIELD = 4;
	public final static int INPUT_COMBOBOX = -8;

	// SERVOY 8
	public final static int MD_INPUT = 47;
	public final static String MD_INPUT_Name = "svy-md-Input";
	
}