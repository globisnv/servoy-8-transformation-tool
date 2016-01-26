package main;

import daos.FileDAO;
import entities.Form;
import exceptions.FormTransformerException;

public class FormTransformer {

	public static void main(String[] args) {
		
		try {
			String path = "C:/Users/Geert/workspaces/servoy7gh_workspace/formTransformer7test/forms";
			Form test = FileDAO.readForm(path);
			/*
			Form oldForm = new Form("{"+test+"}");
			System.out.println(oldForm);*/
			Form newForm = test.transform7to8();
			
			FileDAO.writeForm("d:/", newForm);
			
			//System.err.println(newForm);
			
		} catch (FormTransformerException e) {
			e.printStackTrace();
		}

	}

}

