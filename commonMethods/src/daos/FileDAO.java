package daos;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import entities.Form;
import entities.LogEntry;
import enums.Filename;
import enums.LogLevel;
import enums.LogType;
import enums.UUIDmap;
import exceptions.CommonMethodException;

public class FileDAO {
	
	public static List<LogEntry> logEntries = new ArrayList<>();

	// CONSTRUCTORS

	public FileDAO() {
	}

	public static Form readForm(String pathFilenameNoExt) throws CommonMethodException {

		int lastIndexOfSlash = pathFilenameNoExt.lastIndexOf('\\');
		String path = pathFilenameNoExt.substring(0, lastIndexOfSlash + 1);
		
		logEntries = new ArrayList<>();

		Form form = null;
		try {
			String frmString = readFile(pathFilenameNoExt + Filename.FORM_EXT);
			UUIDmap.scanForImmutableUuids(frmString);
			form = new Form("{" + frmString + "}", path);
			logEntries.add(new LogEntry(LogLevel.INFO, LogType.READ, form, "servoy frm doc"));
			UUIDmap.scanForParentUuids(frmString);
			
			// read .js file
			if (Files.exists(Paths.get(pathFilenameNoExt + Filename.JS_EXT))) {
				form.setJsFile(readFile(pathFilenameNoExt + Filename.JS_EXT));
				logEntries.add(new LogEntry(LogLevel.INFO, LogType.READ, form, "javascript doc"));
			}
		} catch (CommonMethodException e) {
			throw new CommonMethodException(e);
		}
		return form;
	}

	public static void writeForm(Form form) throws CommonMethodException {

		logEntries = new ArrayList<>();
		String pathAndFilename = form.getPath() + form.getName();

		try {
			String outputFrm = form.toServoyForm();
			String outputJS = form.getJsFile();

			if (outputFrm != null) {
				for (Entry<String, String> entry : UUIDmap.getUuidmap().entrySet()) {
					outputFrm = outputFrm.replace(entry.getKey(), entry.getValue());
				}
			}
			if (outputJS != null) {
				for (Entry<String, String> entry : UUIDmap.getUuidmap().entrySet()) {
					outputJS = outputJS.replace(entry.getKey(), entry.getValue());
				}
			}

			writeFile(pathAndFilename + Filename.FORM_EXT, outputFrm);
			logEntries.add(new LogEntry(LogLevel.INFO, LogType.WRITE, form, "write servoy form"));
			if (form.getJsFile() != null) {
				writeFile(pathAndFilename + Filename.JS_EXT, outputJS);
				logEntries.add(new LogEntry(LogLevel.INFO, LogType.WRITE, form, "write javascript file"));
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
		
		logEntries = new ArrayList<>();
		
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

				// skip file if starts with ng$
				String fileNameNoPath = fileAbsPath.substring(fileAbsPath.lastIndexOf('\\') + 1);
				if (fileNameNoPath.startsWith(Filename.NG_PREFIX)) {
					logEntries.add(new LogEntry(LogLevel.DEBUG, LogType.INVENTORY, null, "skipped because of prefix " + Filename.NG_PREFIX + ": " + file.getName()));
					continue;
				}
				// skip file if starts with js$
				if (fileNameNoPath.startsWith(Filename.JS_PREFIX)) {
					logEntries.add(new LogEntry(LogLevel.DEBUG, LogType.INVENTORY, null, "skipped because of prefix " + Filename.JS_PREFIX + ": " + file.getName()));
					continue;
				}
				// skip file if starts with tmp$
				if (fileNameNoPath.startsWith(Filename.TMP_PREFIX)) {
					logEntries.add(new LogEntry(LogLevel.DEBUG, LogType.INVENTORY, null, "skipped because of prefix " + Filename.TMP_PREFIX + ": " + file.getName()));
					continue;
				}

				/*
				 * String newNgFilename = fileAbsPath.replace('\\' +
				 * file.getName(), '\\' + Filename.NG_PREFIX + file.getName());
				 * 
				 * String newJsFilename = fileAbsPath.replace('\\' +
				 * file.getName(), '\\' + Filename.JS_PREFIX + file.getName());
				 * 
				 * if (!Files.exists(Paths.get(newNgFilename)) &&
				 * !Files.exists(Paths.get(newJsFilename))) { int lenghtNoExt =
				 * file.getAbsolutePath().lastIndexOf('.');
				 * pathsNoExt.add(file.getAbsolutePath().toLowerCase().substring
				 * (0, lenghtNoExt)); } else { String frmString =
				 * readFile(file.getAbsolutePath());
				 * FormTransformer.scanForImmutableUuids(frmString); }
				 */

				int lenghtNoExt = file.getAbsolutePath().lastIndexOf('.');
				pathsNoExt.add(file.getAbsolutePath().toLowerCase().substring(0, lenghtNoExt));
				logEntries.add(new LogEntry(LogLevel.INFO, LogType.INVENTORY, null, "added: " + file.getName()));
			}
		}
		System.out.println("Scanned : " + dir + " = " + pathsNoExt.size());
		return pathsNoExt;
	}

	public static void writeLog(String path, List<LogEntry> log) throws CommonMethodException {

		String filename = path + "_" + String.valueOf(new Date().getTime()) + ".csv";
		if (Files.exists(Paths.get(filename))) {
			throw new CommonMethodException("File [" + filename + "] already exists !\n");
		}

		StringBuilder builder = new StringBuilder();
		//builder.append("ERRORS : " + log.containsValue(false) + CharValues.CRLF);
		LogEntry header = new LogEntry(null, null, null, null);
		builder.append(header.headerToString());
		for (LogEntry entry : log) {
			if (entry.log()) {
				builder.append(entry.toString());
			}
		}
		/*
		builder.append(CharValues.CRLF);
		builder.append("NOTHING TO DO : " + log.containsValue(true) + CharValues.CRLF);
		for (Entry<String, Boolean> entry : log.entrySet()) {
			if (entry.getValue()) {
				builder.append(entry.getKey() + CharValues.CRLF);
			}
		}
*/
		try {
			Files.write(Paths.get(filename), builder.toString().getBytes("utf-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
			System.out.println("Log :  " + filename);
		} catch (IOException e) {
			throw new CommonMethodException(e);
		}
	}

	public static <F extends Form> void replaceOriginalByTMPform(F tmpForm) throws CommonMethodException {
		
		List<LogEntry> replaceLogEntries = new ArrayList<>();
		
		if (!tmpForm.getName().startsWith(Filename.TMP_PREFIX)) {
			throw new CommonMethodException("Illegal argument: " + tmpForm.getName());
		}
		Path tmpFrmPathAndFilename = Paths.get(tmpForm.getPath() + tmpForm.getName() + Filename.FORM_EXT);
		Path tmpJsPathAndFilename = Paths.get(tmpForm.getPath() + tmpForm.getName() + Filename.JS_EXT);
		String originalFormName = tmpForm.getName().substring(Filename.TMP_PREFIX.length());
		Path originalFrmPathAndFilename = Paths.get(tmpForm.getPath() + originalFormName + Filename.FORM_EXT);
		Path originalJsPathAndFilename = Paths.get(tmpForm.getPath() + originalFormName + Filename.JS_EXT);

		// delete both original & tmp$
		try {
			Files.delete(originalFrmPathAndFilename);
			replaceLogEntries.add(new LogEntry(LogLevel.DEBUG, LogType.DELETE, null, "Delete original: " + originalFormName + Filename.FORM_EXT));
			if (Files.deleteIfExists(originalJsPathAndFilename)) {
				replaceLogEntries.add(new LogEntry(LogLevel.DEBUG, LogType.DELETE, null, "Delete original: " + originalFormName + Filename.JS_EXT));
			}
			Files.delete(tmpFrmPathAndFilename);
			replaceLogEntries.add(new LogEntry(LogLevel.DEBUG, LogType.DELETE, tmpForm, "Delete temp: servoy form doc"));
			Files.delete(tmpJsPathAndFilename);
			replaceLogEntries.add(new LogEntry(LogLevel.DEBUG, LogType.DELETE, tmpForm, "Delete temp: javascript doc"));
		} catch (IOException e) {
			throw new CommonMethodException(e);
		}
		
		// tmpForm has no element & is not parentForm ? original form can be deleted, only js$ is kept !
		if (!tmpForm.hasElements() && !UUIDmap.isParentForm(tmpForm.getUUID())) {
			replaceLogEntries.add(new LogEntry(LogLevel.WARNING, LogType.DELETE, tmpForm, "Orignal replaced by js$form: orginal had no elements and is not a parent form"));
			logEntries = replaceLogEntries;
			return;
		}
		
		// change tmp$ form.name & write
		tmpForm.setTMPnameToOriginalName();
		FileDAO.writeForm(tmpForm);
		replaceLogEntries.addAll(logEntries);
		logEntries = replaceLogEntries;
	}
	
	public static boolean fileWithPrefixJSexists(Form form) {
		
		logEntries = new ArrayList<>();
		String filename = Filename.JS_PREFIX + form.getName() + Filename.FORM_EXT;
		String path = form.getPath() + filename;
		Boolean exists = Files.exists(Paths.get(path));
		if (exists) {
			logEntries.add(new LogEntry(LogLevel.DEBUG, LogType.SKIPPED, form, "Allready exists: " + filename));
		}
		
		return exists;
	}

}
