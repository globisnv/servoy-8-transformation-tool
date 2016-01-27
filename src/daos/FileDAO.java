package daos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.Form;
import exceptions.FormTransformerException;

public class FileDAO {

	//private final static String FILE_NAME = "labelAndInputForm7";
	//private final static String FILE_NAME = "testForm";
	private final static String FILE_NAME = "labelAndCheckBox7";
	private final static String FORM_EXT = ".frm";
	private final static String JS_EXT = ".js";

	// CONSTRUCTORS

	public FileDAO() {
	}

	public static Form readForm(String path) throws FormTransformerException {

		String pathAndFilename = path + "\\" + FILE_NAME;

		Form form = null;
		try {
			form = new Form("{" + readFile(pathAndFilename + FORM_EXT) + "}");
			if (Files.exists(Paths.get(pathAndFilename + JS_EXT))) {
				form.setJsFile(readFile(pathAndFilename + JS_EXT));
			}
		} catch (FormTransformerException e) {
			throw new FormTransformerException(e);
		}
		return form;
	}

	public static void writeForm(String path, Form form) throws FormTransformerException {

		String pathAndFilename = path + "\\" + form.getName();

		try {
			String outputFrm = form.toServoyForm();
			String outputJS = form.getJsFile();
			Map<String, String> replaceMehtodIDs = findMethodUUIDs(outputFrm);
			if (outputFrm != null) {
				for (Entry<String, String> entry : replaceMehtodIDs.entrySet()) {
					outputFrm = outputFrm.replace(entry.getKey(), entry.getValue());
				}
			}
			if (outputJS != null) {
				for (Entry<String, String> entry : replaceMehtodIDs.entrySet()) {
					outputJS = outputJS.replace(entry.getKey(), entry.getValue());
				}
			}
			
			writeFile(pathAndFilename + FORM_EXT, outputFrm);
			if (form.getJsFile() != null) {
				writeFile(pathAndFilename + JS_EXT, outputJS);
			}
		} catch (FormTransformerException e) {
			throw new FormTransformerException(e);
		}
	}

	private static String readFile(String pathAndFilename) throws FormTransformerException {
		byte[] encoded;

		try {
			encoded = Files.readAllBytes(Paths.get(pathAndFilename));
			String diryString = new String(encoded, StandardCharsets.UTF_8);

			diryString = diryString.replaceAll("\\\\\"'", "'");
			diryString = diryString.replaceAll("'\\\\\"", "'");
			diryString = diryString.replaceAll("\\\\\n", "");

			return diryString;
		} catch (IOException e) {
			throw new FormTransformerException(e);
		}

	}

	private static void writeFile(String pathAndFilename, String fileContent) throws FormTransformerException {
		try {
			Files.write(Paths.get(pathAndFilename), fileContent.getBytes("utf-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new FormTransformerException(e);
		}
	}

	private static Map<String, String> findMethodUUIDs(String string) {

		//System.out.println("findMethodUUIDs = \n"+string);
		Map<String, String> methodIDs = new HashMap<>();
		Pattern REG_EX = Pattern.compile("MethodID:.{1,2}([0-9A-Za-z-]{36})");
		Matcher m = REG_EX.matcher(string);

		while (m.find()) {
		    methodIDs.put(m.group(1), UUID.randomUUID().toString());
		}
		return methodIDs;
	}

}
