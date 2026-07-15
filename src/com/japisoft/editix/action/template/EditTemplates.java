// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.template;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.japisoft.editix.action.file.SelectTemplatePanel;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.document.TemplateInfo;
import com.japisoft.editix.document.TemplateModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.ClosableAction;
import com.japisoft.framework.dialog.actions.DialogAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

/**
 * Edit available templates
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class EditTemplates extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		SelectTemplatePanel stp = new SelectTemplatePanel( false );
		DialogActionModel dam = new DialogActionModel(
			new DialogAction[] {
				DialogActionModel.DEFAULT_CANCELACTION,
				new ResetDefaultAction(),
				new EditAction()
			}
		);
		
		int resultDialog = DialogManager.showDialog( 
				EditixFrame.THIS, 
				"Edit templates", 
				"Edit current templates", 
				"User templates are located at " + TemplateModel.getUserTemplates() + "\nIf you reset a custom user template, it will delete it", 
				null, 
				stp, 
				dam, 
				null );

		TemplateInfo ti = null;
		
		if ( resultDialog != DialogManager.CANCEL_ID ) {
			
			ti = stp.getTemplateInfo();
			if ( ti == null ) {
				EditixFactory.buildAndShowWarningDialog( "No selected template ?" );
				return;
			}
			
		}
		
		if ( resultDialog == 200 ) {

			if ( EditixFactory.buildAndShowConfirmDialog( "Reset to the default content ?" ) ) {
				File f = TemplateModel.getTemplatePath( ti.location );
				f.delete();
			}	

		} else
		
		if ( resultDialog == 100 ) {
			String location = ti.location;

			XMLDocumentInfo doc = 
				DocumentModel.getDocumentForType( ti.type );
			IXMLPanel panel = 
				EditixFactory.getPanelForDocument( doc );
			XMLContainer container = 
				panel.getMainContainer();
			XMLDocumentInfo newDoc = 
				doc.cloneDocument();
			try {
				TemplateModel.resolveTemplate( null, location, newDoc );
				container.setDocumentInfo( newDoc );
				container.setText( newDoc.getTemplate() ); 
				container.setCurrentDocumentLocation( TemplateModel.getTemplatePath( location ).toString() );
				
				EditixFrame.THIS.addContainer( panel );
			} catch( Throwable th ) {
				EditixFactory.buildAndShowErrorDialog( "Can't load this template " + th.getMessage() );					
			}		
		}
		
		if ( resultDialog != DialogManager.CANCEL_ID ) {
			TemplateModel.loadModel();
		}
		
	}

	class EditAction extends AbstractAction implements DialogAction, ClosableAction {
		public EditAction() {
			putValue( Action.NAME, "Edit" );
		}
		public void actionPerformed(ActionEvent e) {
		}
		public int getActionId() {
			return 100;
		}
		public boolean isForDialogFooter() {
			return true;
		}
		public boolean isSpecial() {
			return false;
		}
	}
	
	class ResetDefaultAction extends AbstractAction implements DialogAction, ClosableAction {
		public ResetDefaultAction() {
			putValue( Action.NAME, "Reset to default" );
		}
		public void actionPerformed(ActionEvent e) {
		}
		public int getActionId() {
			return 200;
		}
		public boolean isForDialogFooter() {
			return true;
		}
		public boolean isSpecial() {
			return false;
		}	
	}
	
}
