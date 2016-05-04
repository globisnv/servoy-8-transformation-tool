package daos;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import entities.Form;
import enums.CharValues;
import enums.Filename;
import exceptions.CommonMethodException;

public class FileDAO {

	// CONSTRUCTORS

	public FileDAO() {
	}

	public static Form readForm(String pathFilenameNoExt) throws CommonMethodException {

		int lastIndexOfSlash = pathFilenameNoExt.lastIndexOf('\\');
		String path = pathFilenameNoExt.substring(0, lastIndexOfSlash + 1);

		Form form = null;
		try {
			String frmString = readFile(pathFilenameNoExt + Filename.FORM_EXT);
			//FormTransformer.scanForImmutableUuids(frmString);
			form = new Form("{" + frmString + "}", path);

			// read .js file
			if (Files.exists(Paths.get(pathFilenameNoExt + Filename.JS_EXT))) {
				form.setJsFile(readFile(pathFilenameNoExt + Filename.JS_EXT));
				//FormTransformer.scanForUuids(form.getJsFile());
			}
		} catch (CommonMethodException e) {
			throw new CommonMethodException(e);
		}
		return form;
	}

	public static void writeForm(Form form) throws CommonMethodException {

		String pathAndFilename = form.getPath() + form.getName();

		try {
			String outputFrm = form.toServoyForm();
			String outputJS = form.getJsFile();
			/*
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
			 */
			writeFile(pathAndFilename + Filename.FORM_EXT, outputFrm);
			if (form.getJsFile() != null) {
				writeFile(pathAndFilename + Filename.JS_EXT, outputJS);
			}
		} catch (CommonMethodException e) {
			throw new CommonMethodException(e);
		}
	}

	private static String readFile(String pathAndFilename) throws CommonMethodException {
		byte[] encoded;

		try {
			encoded = Files.readAllBytes(Paths.get(pathAndFilename));
			String diryString = new String(encoded, StandardCharsets.UTF_8);

			diryString = diryString.replaceAll("\\\\\"'", "'");
			diryString = diryString.replaceAll("'\\\\\"", "'");
			diryString = diryString.replaceAll("\\\\\n", "");

			return diryString;
		} catch (IOException e) {
			throw new CommonMethodException(e);
		}

	}

	private static void writeFile(String pathAndFilename, String fileContent) throws CommonMethodException {
		if (Files.exists(Paths.get(pathAndFilename))) {
			throw new CommonMethodException("File [" + pathAndFilename + "] already exists !\n");
		}
		try {
			Files.write(Paths.get(pathAndFilename), fileContent.getBytes("utf-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new CommonMethodException(e);
		}
	}

	public static Set<String> scanStructure(String path) {
		File dir = new File(path);
		if (dir.getName().startsWith(".", 0)) {
			return null;
		}
		Set<String> pathsNoExt = new HashSet<>();
		Collection<File> files = FileUtils.listFilesAndDirs(new File(path), TrueFileFilter.INSTANCE,
				DirectoryFileFilter.DIRECTORY);
		for (File file : files) {
			if (file.getAbsolutePath().contains("\\.")) {
				continue;
			}
			String fileAbsPath = file.getAbsolutePath();
			if (file.isDirectory() && !file.equals(dir)) {
				pathsNoExt.addAll(scanStructure(fileAbsPath));
			}
			if (!file.isDirectory() && Pattern.matches(".+\\.frm$", file.getName().toLowerCase())) {
				String newNgFilename = fileAbsPath.replace('\\' + file.getName(),
						'\\' + Filename.NG_PREFIX + file.getName());
				String fileNameNoPath = fileAbsPath.substring(fileAbsPath.lastIndexOf('\\') + 1);
				if (fileNameNoPath.startsWith(Filename.NG_PREFIX)) {
					continue;
				}
				if (!Files.exists(Paths.get(newNgFilename))) {
					int lenghtNoExt = file.getAbsolutePath().lastIndexOf('.');
					pathsNoExt.add(file.getAbsolutePath().toLowerCase().substring(0, lenghtNoExt));
				} else {
					String frmString = readFile(file.getAbsolutePath());
					//FormTransformer.scanForImmutableUuids(frmString);
				}
			}
		}
		System.out.println("Scanned : " + dir + " = " + pathsNoExt.size());
		return pathsNoExt;
	}
	
	public static void writeLog(String path, Map<String, Boolean> log) throws CommonMethodException {
		
		String filename = path + "formTransformer_" + String.valueOf(new Date().getTime()) + ".log";
		if (Files.exists(Paths.get(filename))) {
			throw new CommonMethodException("File [" + filename + "] already exists !\n");
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("ERRORS : "+log.containsValue(false)+CharValues.CRLF);
		for (Entry<String, Boolean> entry : log.entrySet()) {
			if (!entry.getValue()) {
				builder.append(entry.getKey()+CharValues.CRLF);
			}
		}
		builder.append(CharValues.CRLF);
		builder.append("NOTHING TO DO : "+log.containsValue(true)+CharValues.CRLF);
		for (Entry<String, Boolean> entry : log.entrySet()) {
			if (entry.getValue()) {
				builder.append(entry.getKey()+CharValues.CRLF);
			}
		}
		
		try {
			Files.write(Paths.get(filename), builder.toString().getBytes("utf-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
			System.out.println("Log :  "+filename);
		} catch (IOException e) {
			throw new CommonMethodException(e);
		}
	}

}