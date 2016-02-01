package daos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;

import entities.Form;
import exceptions.FormTransformerException;
import main.FormTransformer;

public class FileDAO {

	//private final static String FILE_NAME = "testForm";
	//private final static String FILE_NAME = "labelAndInputForm7";
	//private final static String FILE_NAME = "labelAndCheckBox7";
	//private final static String FILE_NAME = "_03labelAndComboBox7";
	//private final static String FILE_NAME = "_04labelAndCalendar7";
	private final static String FILE_NAME = "_05labelAndTextArea7";
	private final static String FORM_EXT = ".frm";
	private final static String JS_EXT = ".js";

	// CONSTRUCTORS

	public FileDAO() {
	}

	public static Form readForm(String path) throws FormTransformerException {

		String pathAndFilename = path + "\\" + FILE_NAME;

		Form form = null;
		try {
			String frmString = readFile(pathAndFilename + FORM_EXT);
			FormTransformer.scanForImmutableUuids(frmString);
			form = new Form("{" + frmString + "}");
			if (Files.exists(Paths.get(pathAndFilename + JS_EXT))) {
				form.setJsFile(readFile(pathAndFilename + JS_EXT));
				FormTransformer.scanForUuids(form.getJsFile());
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
			
			if (outputFrm != null) {
				for (Entry<String, String> entry : FormTransformer.getUuidMap().entrySet()) {
					outputFrm = outputFrm.replace(entry.getKey(), entry.getValue());
				}
			}
			if (outputJS != null) {
				for (Entry<String, String> entry : FormTransformer.getUuidMap().entrySet()) {
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


}
