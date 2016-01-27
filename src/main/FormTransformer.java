package main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import daos.FileDAO;
import entities.Form;
import exceptions.FormTransformerException;

public class FormTransformer {

	private static Map<String, String> uuidMap = new HashMap<>();

	public static void main(String[] args) {

		try {
			String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms";
			Form test = FileDAO.readForm(path);
			Form newForm = test.transform7to8();

			FileDAO.writeForm("d:/", newForm);

			System.err.println(newForm);

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
			if (!uuidMap.containsKey(uuid)) {
				uuidMap.put(uuid, UUID.randomUUID().toString());
			}
		}
	}

}
