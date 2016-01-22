package main;

import daos.FileDAO;
import entities.Form;
import exceptions.FormTransformerException;

public class FormTransformer {

	public static void main(String[] args) {
		
		try {
			String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms/testForm.frm";
			//String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms/testForm8.txt";
			//String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms/testForm.txt";
			String test = FileDAO.readFile(path);
			
			Form form = new Form(test);
			
		} catch (FormTransformerException e) {
			e.printStackTrace();
		}

	}

}

