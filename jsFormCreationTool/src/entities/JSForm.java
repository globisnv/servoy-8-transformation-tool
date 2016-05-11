package entities;

import java.util.HashMap;
import java.util.HashSet;

import daos.FileDAO;
import enums.CharValues;
import enums.ElementTypeID;
import enums.Filename;
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

		// CRITERIA for js$Form creation
		if (!oldForm.hasElements()) {
			//System.out.println("no elem :  " + oldForm.name);
			return null;
		}
		if (FileDAO.fileWithPrefixJSexists(oldForm)) {
			return null;
		}
		// ^^^ CRITERIA
		
		JSForm newForm = new JSForm(Filename.JS_PREFIX + oldForm.name, ElementTypeID.FORM, oldForm.path);
		newForm.otherProperties = new HashMap<>(oldForm.otherProperties);
		
		if (oldForm.jsFile != null) {
			newForm.jsFile = new String(JSForm.jscommentsOfJSform(oldForm.name+ Filename.FORM_EXT) + oldForm.jsFile);
		} else {
			newForm.jsFile = new String(JSForm.jscommentsOfJSform(oldForm.name+ Filename.FORM_EXT));
		}
		
		oldForm.setTransformedTrue();
		return newForm;
	}
	
	public static JSForm createTMPform(Form oldForm, String jsFormUUID) throws JSFormCreationException {
		
		// CRITERIA for tmp$Form creation
		if (!oldForm.hasElements()) {
			return null;
		}
		if (FileDAO.fileWithPrefixJSexists(oldForm)) {
			return null;
		}
		// ^^^ CRITERIA
		
		JSForm newForm = new JSForm(Filename.TMP_PREFIX + oldForm.name, ElementTypeID.FORM, oldForm.path);
		newForm.otherProperties = new HashMap<>(oldForm.otherProperties);
		if (jsFormUUID != null) {
			newForm.otherProperties.put("extendsID", jsFormUUID);
		}
		newForm.items = new HashSet<>(oldForm.items);
		newForm.jsFile = new String(JSForm.jscommentsOfTMPform(Filename.JS_PREFIX + oldForm.name + Filename.JS_EXT));
		
		oldForm.setTransformedTrue();
		return newForm;
	}
	
	
	private static String jscommentsOfJSform(String contentMovedFromFormName) {
		StringBuilder comments = new StringBuilder();
		comments.append("// DO NOT REMOVE COMMENTS !  ")
		.append("Contains jsCode for child form = ")
		.append(contentMovedFromFormName).append(CharValues.CRLF)
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
