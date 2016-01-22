package main;

import daos.FileDAO;
import exceptions.FormTransformerException;

public class FormTransformer {

	public static void main(String[] args) {
		try {
		String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms/testForm.frm";
		String test;
		
			test = FileDAO.readFile(path);
			System.out.println(test);
		} catch (FormTransformerException e) {
			e.printStackTrace();
		}

	}

}
