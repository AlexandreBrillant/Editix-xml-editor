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

package com.japisoft.editix.editor.js.action;

import java.awt.event.ActionEvent;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.AbstractAction;

import com.japisoft.editix.editor.js.JSContainer;
import com.japisoft.editix.javascript.JavaScriptFactory;
import com.japisoft.editix.plugin.EditiXManager;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.error.ErrorManager;

public class CheckAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {
		JSContainer jsc = ( JSContainer )EditixFrame.THIS.getSelectedContainer();
		String text = jsc.getText();
		ErrorManager rm = jsc.getErrorManager();
		rm.notifyNoError(false);
		rm.initErrorProcessing();

		try {
			ScriptEngine engine = JavaScriptFactory.newFactory().engine();	
			engine.eval( text );
			EditixFactory.buildAndShowInformationDialog( "Your document is correct" );
		} catch( ScriptException se ) {
			int line = se.getLineNumber();
			String message = se.getMessage();
			int i = message.lastIndexOf( ":" );
			if ( i > -1 )
				message = message.substring( i + 1 );
			rm.notifyError( this, true, null, line, se.getColumnNumber(), -1, message, false );
			EditixFactory.buildAndShowErrorDialog( "Error(s) found" );
		}
		rm.stopErrorProcessing();
	}

}
