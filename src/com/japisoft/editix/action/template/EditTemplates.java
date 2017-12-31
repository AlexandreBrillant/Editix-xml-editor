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
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.ClosableAction;
import com.japisoft.framework.dialog.actions.DialogAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class EditTemplates extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		SelectTemplatePanel stp = new SelectTemplatePanel();
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
