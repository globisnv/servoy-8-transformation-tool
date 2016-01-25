package daos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import exceptions.FormTransformerException;

public class FileDAO {

	// CONSTRUCTORS

	public FileDAO() {
	}

	public static String readFile(String path) throws FormTransformerException {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
			String diryString = new String(encoded, StandardCharsets.UTF_8);

			diryString = diryString.replaceAll("\\\\\"'", "'");
			diryString = diryString.replaceAll("'\\\\\"", "'");
			diryString = diryString.replaceAll("\\\\\n", "");

			return diryString;
		} catch (IOException e) {
			throw new FormTransformerException(e);
		}

	}

	public static void writeFile(String path) throws FormTransformerException {
		try {
			Files.write(Paths.get(path), "My string to save".getBytes("utf-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new FormTransformerException(e);
		}
	}

}
