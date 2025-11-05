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

package com.japisoft.editix.action.script;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.japisoft.editix.script.BasicScript;
import com.japisoft.editix.script.ScriptAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;

public class TestScript extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		if ( EditixFrame.THIS.getSelectedContainer().getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "Please save your script before testing" );
			return;
		}
		runScript( e.getSource(),new File( EditixFrame.THIS.getSelectedContainer().getCurrentDocumentLocation() ) );
	}
	
	public static void runScript( Object source, File scriptFile ) {
		BasicScript script = new BasicScript( "Test", scriptFile, "" );
		ScriptAction sa = new ScriptAction( script );
		sa.actionPerformed( new ActionEvent( source, 0, "" ) );
		if ( !sa.errorFound )
			EditixFactory.buildAndShowInformationDialog( "Script terminated" );		
	}

}

