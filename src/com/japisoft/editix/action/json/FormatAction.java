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

package com.japisoft.editix.action.json;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.json.JSONObject;

import com.japisoft.editix.editor.json.JSONContainer;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;

public class FormatAction extends AbstractAction {

	static void format( JSONContainer jsc ) throws Exception {
		String text = jsc.getText();
		JSONObject obj = new JSONObject( text );
		jsc.setText( obj.toString( 1 ) );		
	}
	
	public void actionPerformed(ActionEvent e) {
		JSONContainer jsc = ( JSONContainer )EditixFrame.THIS.getSelectedContainer();
		try {
			format( jsc );
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't format your document, check for the syntax" );
		}
		
	}

}
