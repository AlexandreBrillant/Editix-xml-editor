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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.document.TemplateInfo;
import com.japisoft.editix.document.TemplateModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.InformationDialog;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.llm.LLMManager;
import com.japisoft.framework.llm.LLMToolkit;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.toolkit.Toolkit;
import com.japisoft.framework.xml.format.FormatterConfig;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.action.ActionModel;

public class NewAction extends AbstractAction {

	public static void prepare( TemplateInfo info ) {
		if ( info.encoding == null )
			info.encoding =   
				Preferences.getPreference( 
					"file", 
					"rw-encoding", 
					Toolkit.FILE_ENCODING )[ 0 ]; 
	}

	public void actionPerformed( ActionEvent e ) {
		TemplateInfo info = getDocumentInfo();
		if ( info != null ) {
			newDocument( info );
		}
	}

	private void processLLMResponse( IXMLPanel panel, XMLDocumentInfo info, String response ) {
		
		Entry<String,String> res = LLMToolkit.extractTypeContent( response );
		if ( res == null ) {
			if ( !EditixFactory.buildAndShowConfirmDialog( "Can't find a document inside the response, continue ?") ) {
				return;
			}
		} else {
			response = res.getValue();
			if ( "XML".equalsIgnoreCase( res.getKey() ) ) {
				if ( !response.contains( "<?xml " )) {
					response = "<?xml version='1.0' encoding='UTF-8'?>\n" + response;
				}
			}
		}

		XMLContainer container = panel.getMainContainer();					
		
		info.setTemplate( response );
		container.setDocumentInfo( info );

		com.japisoft.xmlpad.action.ActionModel.activeActionByName(
			ActionModel.NEW_ACTION,
			container,
			container.getEditor()
		);		
		
		EditixFrame.THIS.addContainer( panel );			
	}

	private void newDocument( TemplateInfo info ) {
		prepare( info );
		signalToUser( info.type );

		String wizardContent = info.startWizard();
		boolean wizardMode = info.hasWizard();

		if ( wizardMode && wizardContent == null )
			return;

		XMLDocumentInfo doc = 
				DocumentModel.getDocumentForType( info.type );
		IXMLPanel panel = 
			EditixFactory.getPanelForDocument( doc );
		XMLContainer container = 
			panel.getMainContainer();
		XMLDocumentInfo newDoc = 
			doc.cloneDocument();

		// Load the template
 		if ( info.location != null ) { 			
 			try {
 				TemplateModel.resolveTemplate(info.encoding, info.location, newDoc); 
 			} catch( Throwable th ) {
 				EditixFactory.buildAndShowErrorDialog( "Can't load " + info.location );
 				th.printStackTrace();
 				return;
 			}
 		}
		
		if ( !wizardMode && info.askLLM( newDoc, ( response ) -> {
			processLLMResponse( panel, newDoc, response );
		} ) ) {
			// skip
		} else {

			container.setDocumentInfo( newDoc );
			
			com.japisoft.xmlpad.action.ActionModel.activeActionByName(
				ActionModel.NEW_ACTION,
				container,
				container.getEditor()
			);
			
			container.setProperty( "encoding", com.japisoft.editix.toolkit.Toolkit.getCurrentFileEncoding() );
			if ( wizardContent != null ) {
				container.setText( wizardContent );
				
				if ( "XML".contentEquals( info.type ) ) {
					FormatAction.format(
						container,
						null,
						new FormatterConfig(),
						null
					);
	
				}
				
				if ( "XSLT".equals( info.type ) ) {
					File source = info.getWizardSource();
					panel.setProperty( "xslt.data.file", source );
				}
			}
					
			EditixFrame.THIS.addContainer( panel );
			container.getEditor().notifyCurrentLocation( true );
		}
	}

	static void signalToUser( String type ) {
		if ( "EXML".equals( type ) ) {
			if ( Preferences.getPreference( "system", "usertype" + type, true ) ) {
				JDialog dialog = new InformationDialog( 
						new JLabel( 
								"Note that you are using a special editing mode for Large XML documents. Choose another document type for common documents", 
								JLabel.LEFT ),		
						"usertype" + type );
				dialog.setVisible( true );
				dialog.dispose();
			}
		}		
	}

	public void setTemplate( TemplateInfo ti ) {
		putValue( "template", ti );
	}

	private TemplateInfo getDocumentInfo() {
		if ( getValue( "template" ) != null )
			return ( TemplateInfo )getValue( "template" );

		SelectTemplatePanel stp = new SelectTemplatePanel();

		if (
			DialogManager.showDialog(
				EditixFrame.THIS, 
				"Create a document",
				"New document", 
				"Select a template for building a new document", 
				null, 
				stp ) == DialogManager.OK_ID ) {

			return stp.getTemplateInfo();

		}

		return null;
	}

}
