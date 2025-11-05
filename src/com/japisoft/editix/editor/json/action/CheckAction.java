// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.editor.json.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.japisoft.editix.editor.json.JSONContainer;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.error.ErrorManager;

public class CheckAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {
		JSONContainer jsc = ( JSONContainer )EditixFrame.THIS.getSelectedContainer();
		String text = jsc.getText();
		ErrorManager rm = jsc.getErrorManager();
		rm.notifyNoError(false);
		rm.initErrorProcessing();
		
		try {
			if ( text.trim().startsWith( "[" ) ) {
				new JSONArray( text );
			} else
				new JSONObject( text );
			EditixFactory.buildAndShowInformationDialog( "Your document is correct" );
		} catch( JSONException exc ) {
			rm.notifyError( this, true, null, exc.line, -1, exc.offset, exc.getMessage(), false );
			EditixFactory.buildAndShowErrorDialog( "Error(s) found" );
		}

		rm.stopErrorProcessing();
	}

}

