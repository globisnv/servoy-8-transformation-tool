package daos;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import entities.Form;
import exceptions.FormTransformerException;
import main.FormTransformer;

public class FileDAO {

	// private final static String FILE_NAME = "testForm";
	// private final static String FILE_NAME = "labelAndInputForm7";
	// private final static String FILE_NAME = "labelAndCheckBox7";
	// private final static String FILE_NAME = "_03labelAndComboBox7";
	// private final static String FILE_NAME = "_04labelAndCalendar7";
	// private final static String FILE_NAME = "_05labelAndTextArea7";
	// private final static String FILE_NAME = "_06labelAndTypeAhead7";
	// private final static String FILE_NAME = "_07labelAndRadio7";
	// private final static String FILE_NAME = "_08labelAndPassword7";
	// private final static String FILE_NAME = "_10labelAndNoLabelOrButton7";
	//private final static String FILE_NAME = "_11tabs";
	private final static String FORM_EXT = ".frm";
	private final static String JS_EXT = ".js";

	// CONSTRUCTORS

	public FileDAO() {
	}

	public static Form readForm(String pathFilenameNoExt) throws FormTransformerException {

		int lastIndexOfSlash = pathFilenameNoExt.lastIndexOf('\\');
		String path = pathFilenameNoExt.substring(0, lastIndexOfSlash+1);

		Form form = null;
		try {
			String frmString = readFile(pathFilenameNoExt + FORM_EXT);
			FormTransformer.scanForImmutableUuids(frmString);
			form = new Form("{" + frmString + "}", path);
			if (Files.exists(Paths.get(pathFilenameNoExt + JS_EXT))) {
				form.setJsFile(readFile(pathFilenameNoExt + JS_EXT));
				FormTransformer.scanForUuids(form.getJsFile());
			}
		} catch (FormTransformerException e) {
			throw new FormTransformerException(e);
		}
		return form;
	}

	public static void writeForm(Form form) throws FormTransformerException {

		String pathAndFilename = form.getPath() + form.getName();

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
		if (Files.exists(Paths.get(pathAndFilename))) {
			throw new FormTransformerException("File [" + pathAndFilename + "] already exists !\n");
		}
		try {
			Files.write(Paths.get(pathAndFilename), fileContent.getBytes("utf-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new FormTransformerException(e);
		}
	}

	public static Set<String> scanStructure(String path) {
		File dir = new File(path);
		Set<String> pathsNoExt = new HashSet<>();
		Collection<File> files = FileUtils.listFilesAndDirs(new File(path), TrueFileFilter.INSTANCE,
				DirectoryFileFilter.DIRECTORY);
		for (File file : files) {
			if (file.isDirectory() && !file.equals(dir)) {
				pathsNoExt.addAll(scanStructure(file.getAbsolutePath()));
			}
			if (!file.isDirectory() && Pattern.matches(".+\\.frm$", file.getName().toLowerCase())) {
				String ngFilename = file.getAbsolutePath().replace('\\' + file.getName(),
						'\\' + FormTransformer.NG_PREFIX + file.getName());
				if (!Files.exists(Paths.get(ngFilename))) {
					int lenghtNoExt = file.getAbsolutePath().lastIndexOf('.');
					pathsNoExt.add(file.getAbsolutePath().toLowerCase().substring(0, lenghtNoExt));
				} else {
					String frmString = readFile(file.getAbsolutePath());
					FormTransformer.scanForImmutableUuids(frmString);
				}
			}
		}

		return pathsNoExt;
	}

}
