package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import daos.FileDAO;
import entities.Form;
import entities.JSForm;
import enums.ElementTypeID;
import enums.Filename;
import enums.UUIDmap;
import exceptions.JSFormCreationException;

// TODO : gekende problemen :
/*
 * methodID = "-1" => -1 (zoniet :  error in UUID string -1
 * forms met java beans = niet ondersteund !  zoeken : javax.swing in *.frm
 * ng$ form heeft ALERTS ?
 */


public class JSFormCreator {

	public static void main(String[] args) {

		try {

			String path = "C:/Users/geert.haegens/workspaces/servoy8testMagWeg/newstructure/forms/";
			//String path = "C:/Users/geert.haegens/workspaces/servoy8new12022016/globis_articles/forms/";
			
			Set<String> pathAndFilenamesNoExt = FileDAO.scanStructure(path);
			System.out.println("Forms to scan :  " + pathAndFilenamesNoExt.size());
			Set<Form> oldForms = new HashSet<>();
			Set<JSForm> newForms = new HashSet<>();
			// true = nothing to do ; false = ERROR
			Map<String, Boolean> logForms = new LinkedHashMap<>();

			// read all forms
			for (String formPathAndFilenamesNoExt : pathAndFilenamesNoExt) {
				oldForms.add(FileDAO.readForm(formPathAndFilenamesNoExt));
			}

			// transform all forms
			
			for (Form oldForm : oldForms) {
				JSForm newJSform = JSForm.createJSform(oldForm);
				if (newJSform != null) {
					UUIDmap.uuidMapAdd(oldForm.getUUID(), newJSform.getUUID());
					newForms.add(newJSform);
					JSForm newTMPform = JSForm.createTMPform(oldForm, newJSform.getUUID());
					if (newTMPform != null) {
						newForms.add(newTMPform);
					}
					oldForm.setTransformedTrue();
				}
				// add log
				if (oldForm.getTypeId() == ElementTypeID.INVALID_TRANSFORMATION) {
					logForms.put(oldForm.getPath() +Filename.FORM_EXT, false);
				}
				if (!oldForm.isTransformed()) {
					logForms.put(oldForm.getPath() + "/" + oldForm.getName() + Filename.FORM_EXT, true);
				}
				
				
			}

			
			// WRITE log file
			FileDAO.writeLog(path + "JSFormCreator", logForms);
			
			
			 // WRITE all js$ & tmp$ forms for (Form newForm : newForms) {
			for (JSForm newForm : newForms) {
				FileDAO.writeForm(newForm);
			}
			
			// IF tmp$ :  delete original + rename tmp$
			
			for (JSForm newForm : newForms) {
				if (newForm.getName().startsWith(Filename.TMP_PREFIX)) {
					FileDAO.replaceOriginalByTMPform(newForm);
				}
				
			}
			
			System.out.println("Forms written :  "+newForms.size());

			System.err.println("Done.");

		} catch (JSFormCreationException e) {
			e.printStackTrace();
		}

	}

	// GETTERS & SETTERS

	

	// OTHERS

	
	
	

}
