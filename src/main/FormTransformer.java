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
import enums.ElementTypeID;
import exceptions.FormTransformerException;

// TODO : mdRadio.spec + .html => valuelist = valuelistID
// TODO : mdRadio.spec  =>containsFormId = containsFormID
// TODO : tabPanel.spec  =>containsFormId = containsFormID

public class FormTransformer {

	private static Map<String, String> uuidMap = new HashMap<>();
	private static Set<String> uuidImmutables = new HashSet<>();
	
	public static final String NG_PREFIX = "ng$";
	public static final String FORM_EXT = ".frm";
	public static final String JS_EXT = ".js";
	public static final String CRLF = System.getProperty("line.separator");
	public static final char QM = '"'; // quotation mark
	public static final String SEARCH_ICON_IMAGEMEDIA_ID = "07a009d2-0a86-49d1-b28c-bff40b764c40";
	
	public static void main(String[] args) {

		try {
			//String path = "C:/Users/geert.haegens/workspaces/servoy8gh29012016ws/S1235/forms";
			//String path = "C:/Users/geert.haegens/workspaces/servoy_workspace8new";
			//String path = "C:/Users/geert.haegens/workspaces/servoy8new05022016/ghaTest/forms";
			String path = "C:/Users/geert.haegens/workspaces/servoy8new12022016/globis_attributes/forms";
			
			
			Set<String> pathAndFilenamesNoExt = FileDAO.scanStructure(path);
			System.out.println("Forms to scan :  "+pathAndFilenamesNoExt.size());
			Set<Form> newForms = new HashSet<>();
			// true = nothing to do ; false = ERROR
			Map<String, Boolean> logForms = new LinkedHashMap<>();
			
			for (String formPathAndFilenamesNoExt : pathAndFilenamesNoExt) {
				Form oldForm = FileDAO.readForm(formPathAndFilenamesNoExt);
				
				Form newForm = oldForm.transform7to8();
				if (oldForm.isTransformed() && newForm != null) {
					uuidMap.put(oldForm.getUUID(), newForm.getUUID());
					newForms.add(newForm);
				}
				// add log
				if (oldForm.getTypeId() == ElementTypeID.INVALID_TRANSFORMATION) {
					logForms.put(formPathAndFilenamesNoExt+FormTransformer.FORM_EXT, false);
				}
				if (!oldForm.isTransformed()) {
					logForms.put(formPathAndFilenamesNoExt+FormTransformer.FORM_EXT, true);
				}
				
			}
			// WRITE log
			FileDAO.writeLog(path, logForms);
			
			// WRITE all ng$ forms
			for (Form newForm : newForms) {
				FileDAO.writeForm(newForm);
			}
			System.out.println("Forms written :  "+newForms.size());
			
			
			System.err.println("Done.");
			
		} catch (FormTransformerException e) {
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
