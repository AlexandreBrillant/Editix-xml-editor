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

package com.japisoft.xmlpad.action.xml;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.RelaxNGValidator;

import com.japisoft.xmlpad.xml.validator.Validator;

/**
 * Refresh action : XML Parsing. This action works using JAXP
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.2 */
public class ParseAction extends XMLAction implements CheckableAction {

	public static final String ID = ParseAction.class.getName();

	public ParseAction() {
		super();
	}

	private static Validator defVal;
	
	public static void setDefaultValidator( Validator val ) {
		defVal = val;
	}
	
	public boolean checkDocument(XMLContainer container, boolean silentMode ) {
		setXMLContainer( container );
		try { 
			return 
				notifyAction( silentMode );
		} finally {
			dispose();
		}
	}	

	public boolean notifyAction() {
		return notifyAction( false );
	}
	
	public boolean notifyAction( boolean silentMode ) {
		container.getErrorManager().flushLastError();
		container.getErrorManager().initErrorProcessing();
		try {
			Validator v = container.getDocumentInfo().getCustomValidator();

			if ( v == null ) {
				if ( container.getSchemaAccessibility().getRelaxNGValidationLocation() != null ) {
					v = new RelaxNGValidator();
				} else {
					if ( defVal != null )
						v = defVal;
					else
						v = new DefaultValidator();
				}
			}

			int res = v.validate( container, silentMode );

			if ( res == Validator.OK ) {
				container.getErrorManager().notifyNoError( false );
				if ( popupOk && !silentMode ) {
					JOptionPane.showMessageDialog( container.getView(), getMessageForOk( container )  );					
				}
			}

			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						container.getEditor().requestFocus();			
					}
				}
			);

			return res == Validator.OK;
		} finally {
			container.getErrorManager().stopErrorProcessing();
		}
	}

	private boolean popupOk = true;

	/** Enabled/Disable a dialog box confirming the parsing step is correct. By default <code>true</code> */
	public void setPopupForOk( boolean popup ) {
		this.popupOk = popup;
	}
	
	private String messageForOk = null;
	
	/** Reset the message when the parsing step is correct */
	public void setMessageForOk( String message ) {
		this.messageForOk = message;
	}

	public String getMessageForOk( XMLContainer container ) {
		if ( messageForOk == null ) {
			messageForOk = container.getLocalizedMessage( "PARSE_OK", "Your document is correct" );
		}
		return messageForOk;
	}

}

