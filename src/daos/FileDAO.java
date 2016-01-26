package daos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import entities.Form;
import exceptions.FormTransformerException;

public class FileDAO {

	private final static String FILE_NAME = "labelAndInputForm7";
	// private final static String FILE_NAME = "testForm";
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
			writeFile(pathAndFilename + FORM_EXT, form.toServoyForm());
			if (form.getJsFile() != null) {
				writeFile(pathAndFilename + JS_EXT, form.getJsFile());
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

}
