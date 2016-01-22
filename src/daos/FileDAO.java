package daos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import exceptions.FormTransformerException;

public class FileDAO {

	// CONSTRUCTORS

	public FileDAO() {
		// TODO Auto-generated constructor stub
	}

	public static String readFile(String path) throws FormTransformerException {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
			String diryString =  new String(encoded, StandardCharsets.UTF_8);
			
			diryString = diryString.replaceAll("\\\\\"'", "'");
			diryString = diryString.replaceAll("'\\\\\"", "'");
			diryString = diryString.replaceAll("\\\\\n", "");
			
			return diryString;
		} catch (IOException e) {
			throw new FormTransformerException(e);
		}
		
	}

}
