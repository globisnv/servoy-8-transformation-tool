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
import exceptions.JSFormCreationException;

// TODO : gekende problemen :
/*
 * methodID = "-1" => -1 (zoniet :  error in UUID string -1
 * forms met java beans = niet ondersteund !  zoeken : javax.swing in *.frm
 * ng$ form heeft ALERTS ?
 */


public class JSFormCreator {

	private static Map<String, String> uuidMap = new HashMap<>();
	private static Set<String> uuidImmutables = new HashSet<>();
	
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
					//uuidMap.put(oldForm.getUUID(), newForm.getUUID());
					newForms.add(newJSform);
					JSForm newTMPform = JSForm.createTMPform(oldForm, newJSform.getUUID());
					if (newTMPform != null) {
						newForms.add(newTMPform);
					}
				}
				// add log
				if (oldForm.getTypeId() == ElementTypeID.INVALID_TRANSFORMATION) {
					logForms.put(oldForm.getPath() +Filename.FORM_EXT, false);
				}
				if (!oldForm.isTransformed()) {
					logForms.put(oldForm.getPath() + "/" + oldForm.getName() + Filename.FORM_EXT, true);
				}
				/*
				switch (oldForm.getView()) {
				case FormView.RECORD_VIEW:
					newForm = oldForm.transform7dtl();
					break;
				case FormView.TABLE_VIEW:
					FormElement parentFe = Form.findParentFormElement(oldForm.getUUID(), oldForms);
					newForm = oldForm.transform7lst(parentFe);
					newForms.add(newForm);
					break;
				default:
					break;
				}
				 */
			}

			
			// WRITE log file
			FileDAO.writeLog(path + "JSFormCreator", logForms);
			
			
			 // WRITE all js$ & tmp$ forms for (Form newForm : newForms) {
			for (JSForm newForm : newForms) {
				FileDAO.writeForm(newForm);
			}
			 
			 System.out.println("Forms written :  "+newForms.size());
			 

			System.err.println("Done.");

		} catch (JSFormCreationException e) {
			e.printStackTrace();
		}

	}

	// GETTERS & SETTERS

	public static Map<String, String> getUuidMap() {
		return uuidMap;
	}

	// OTHERS

	public static void scanForUuids(String string) {
		Pattern REG_EX = Pattern.compile("uuid:.([-0-9A-Za-z]{36})");
		Matcher m = REG_EX.matcher(string);
		while (m.find()) {
			String uuid = m.group(1);
			if (!uuidImmutables.contains(uuid) && !uuidMap.containsKey(uuid)) {
				uuidMap.put(uuid, UUID.randomUUID().toString());
			}
		}
	}

	public static void scanForImmutableUuids(String string) {
		// valuelistIDs = immutable
		Pattern REG_EX = Pattern.compile("valuelistID:.([-0-9A-Za-z]{36})");
		Matcher m = REG_EX.matcher(string);
		while (m.find()) {
			uuidImmutables.add(m.group(1));
		}
	}
	
	

}
