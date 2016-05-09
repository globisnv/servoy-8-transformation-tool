package entities;

import java.util.HashMap;
import java.util.HashSet;

import enums.CharValues;
import enums.ElementTypeID;
import enums.Filename;
import enums.UUIDmap;
import exceptions.JSFormCreationException;

public class JSForm extends Form {

	// CONSTRUCTORS

	protected JSForm(String name, int typeid, String path) {
		super(name, typeid, path);
	}
	

	// OTHERS
	
	public static JSForm createJSform(Form oldForm) throws JSFormCreationException {

		if (oldForm.isTransformed()) {
			return null;
		}

		// TODO : CRITERIA for js$Form creation
		
		JSForm newForm = new JSForm(Filename.JS_PREFIX + oldForm.name, ElementTypeID.FORM, oldForm.path);
		newForm.otherProperties = new HashMap<>(oldForm.otherProperties);
		// keep only element BODY
		newForm.items = new HashSet<>();
		for (FormElement element : oldForm.items) {
			if (element.typeid == ElementTypeID.BODY) {
				FormElement newElement = new FormElement(element.name, element.typeid);
				newElement.otherProperties = new HashMap<>(element.otherProperties);
				newForm.items.add(newElement);
				break;
			}
		}
		if (oldForm.jsFile != null) {
			newForm.jsFile = new String(JSForm.jscommentsOfJSform(oldForm.name+ Filename.JS_EXT) + oldForm.jsFile);
		} else {
			newForm.jsFile = new String(JSForm.jscommentsOfJSform(oldForm.name+ Filename.JS_EXT));
		}
		
		return newForm;
	}
	
	public static JSForm createTMPform(Form form, String jsFormUUID) throws JSFormCreationException {

		JSForm newForm = new JSForm(Filename.TMP_PREFIX + form.name, ElementTypeID.FORM, form.path);
		newForm.otherProperties = new HashMap<>(form.otherProperties);
		newForm.otherProperties.put("extendsID", jsFormUUID);
		newForm.items = new HashSet<>(form.items);
		newForm.jsFile = new String(JSForm.jscommentsOfTMPform(Filename.JS_PREFIX + form.name + Filename.JS_EXT));
		
		return newForm;
	}
	
	
	private static String jscommentsOfJSform(String contentMovedFromFilename) {
		StringBuilder comments = new StringBuilder();
		comments.append("// DO NOT REMOVE COMMENTS").append(CharValues.CRLF)
		.append("// Old structure filename = ")
		.append(contentMovedFromFilename).append(CharValues.CRLF)
		.append("//").append(CharValues.CRLF);
		return comments.toString();
	}
	
	private static String jscommentsOfTMPform(String contentMovedToFilename) {
		StringBuilder comments = new StringBuilder();
		comments.append("// IMPORTANT: DO NOT ALTER THE CONTENT OF THIS FILE !").append(CharValues.CRLF)
				.append("// New structure filename = ")
				.append(contentMovedToFilename).append(CharValues.CRLF)
				.append("//").append(CharValues.CRLF);
		return comments.toString();
	}

}
