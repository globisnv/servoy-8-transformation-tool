package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import daos.FileDAO;
import entities.Form;
import exceptions.FormTransformerException;

public class FormTransformer {

	private static Map<String, String> uuidMap = new HashMap<>();
	private static Set<String> uuidImmutables = new HashSet<>();
	public static String NG_PREFIX = "ng$";

	public static void main(String[] args) {

		try {
			String path = "C:/Users/geert.haegens/workspaces/servoy8gh29012016ws";
			
			Set<String> pathAndFilenamesNoExt = FileDAO.scanStructure(path);
			
			for (String formPathAndFilenamesNoExt : pathAndFilenamesNoExt) {
				Form oldForm = FileDAO.readForm(formPathAndFilenamesNoExt);
				Form newForm = oldForm.transform7to8();
				FileDAO.writeForm(newForm);
				
			}
			System.out.println("\nForms written :  "+pathAndFilenamesNoExt.size());
			
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
