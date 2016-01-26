package main;

import daos.FileDAO;
import entities.Form;
import exceptions.FormTransformerException;

public class FormTransformer {

	public static void main(String[] args) {
		
		try {
			String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms/labelAndInputForm7.frm";
			//String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms/testForm.frm";
			String test = FileDAO.readFile(path);
			
			Form oldForm = new Form("{"+test+"}");
			System.out.println(oldForm);
			Form newForm = oldForm.transform7to8();
			
			FileDAO.writeFile("d:/outputTest8Form.frm", newForm.toServoyForm());
			
			System.err.println(newForm);
			
		} catch (FormTransformerException e) {
			e.printStackTrace();
		}

	}

}

