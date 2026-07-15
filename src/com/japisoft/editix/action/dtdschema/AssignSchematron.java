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

package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

public class AssignSchematron extends AbstractAction {

	public static final String PI = "<?schematron";
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		File f = FileManager.getSelectedFile( true, "sch", "Schematron file" );
		
		if ( f != null ) {
			XMLPadDocument doc = container.getXMLDocument();
			int next = Math.max( 0, doc.nextTag( 0 ) - 1 );
			try {
				doc.insertString( next, "<?schematron path=\"" + f.toString() + "\"?>", null );
			} catch( BadLocationException exc ) {
			}
		}
	}

}
