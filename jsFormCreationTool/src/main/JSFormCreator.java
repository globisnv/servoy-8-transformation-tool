package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import daos.FileDAO;
import entities.Element;
import entities.Form;
import entities.JSForm;
import entities.LogEntry;
import enums.ElementTypeID;
import enums.Filename;
import enums.LogLevel;
import enums.LogType;
import enums.UUIDmap;
import exceptions.JSFormCreationException;

// TODO : gekende problemen :
/*
 * methodID = "-1" => -1 (zoniet :  error in UUID string -1
 * forms met java beans = niet ondersteund !  zoeken : javax.swing in *.frm
 * ng$ form heeft ALERTS ?
 */


public class JSFormCreator {
	
	public static List<LogEntry> logEntries = new ArrayList<>();

	public static void main(String[] args) {

		try {
			
			// do not apply a default name if element has none
			Element.setAllowNullableName(true);
			// on writing js$ forms: replace @private anotation by @protected
			Form.setReplacePrivateByProtected(true);
			
			//String path = "C:/Users/geert.haegens/workspaces/GO8ws_DO_NOT_USE/globis_billing/forms";
			String path = "C:/Users/geert.haegens/workspaces/GO8ws_DO_NOT_USE/globis_purchase/forms";
			//String path = "C:/Users/geert.haegens/workspaces/servoy8testMagWeg/struct_multiparents/forms/";
			
			Set<String> pathAndFilenamesNoExt = FileDAO.scanStructure(path);
			logEntries.addAll(FileDAO.logEntries);
			System.out.println("Forms to scan :  " + pathAndFilenamesNoExt.size());
			Set<Form> oldForms = new HashSet<>();
			Set<JSForm> newForms = new HashSet<>();
			
			// read all forms
			for (String formPathAndFilenamesNoExt : pathAndFilenamesNoExt) {
				oldForms.add(FileDAO.readForm(formPathAndFilenamesNoExt));
				logEntries.addAll(FileDAO.logEntries);
			}
			
			// transform all forms
			
			String parentUuid = null;
			for (Form oldForm : oldForms) {
				JSForm newJSform = JSForm.createJSform(oldForm);
				
				if (newJSform != null) {
					newForms.add(newJSform);
					parentUuid = newJSform.getUUID();
					JSForm newTMPform = JSForm.createTMPform(oldForm, parentUuid);
					if (newTMPform != null) {
						UUIDmap.uuidMapAdd(newTMPform.getUUID(), oldForm.getUUID());
						newForms.add(newTMPform);
					}
				}
					
				// add log
				if (oldForm.getTypeId() == ElementTypeID.INVALID_TRANSFORMATION) {
					logEntries.add(new LogEntry(LogLevel.ERROR, LogType.CREATE, oldForm, "INVALID_TRANSFORMATION"));
				}
				/*
				if (!oldForm.isTransformed()) {
					logEntries.add(new LogEntry(LogLevel.ERROR, LogType.CREATE, oldForm, "TRANSFORMATION FAILED"));
				}*/
				logEntries.addAll(oldForm.logEntries);
				
			}
			
			 // WRITE all js$ & tmp$ forms for (Form newForm : newForms) {
			for (JSForm newForm : newForms) {
				FileDAO.writeForm(newForm);
				logEntries.addAll(FileDAO.logEntries);
			}
			
			// IF tmp$ :  delete original + rename tmp$
			
			for (JSForm newForm : newForms) {
				if (newForm.getName().startsWith(Filename.TMP_PREFIX)) {
					FileDAO.replaceOriginalByTMPform(newForm);
					logEntries.addAll(FileDAO.logEntries);
				}
			}
			
			
			// WRITE log file
			FileDAO.writeLog(path + "JSFormCreator", logEntries);
						
			System.out.println("Forms written :  " + newForms.size());

			System.err.println("Done.");

		} catch (JSFormCreationException e) {
			e.printStackTrace();
		}

	}

	
	

}
