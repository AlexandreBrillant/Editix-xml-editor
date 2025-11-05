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

package com.japisoft.editix.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.about.AboutDialog;
import com.japisoft.p3.Manager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class AboutAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {

		DialogManager.resetDefaultSize( new Dimension( 370, 500 ) );

		String message = 
			Manager.hasValidRegisteredFile() ? ( "Registered version by " + Manager.getUser() ) : " 30 Day Evaluation Version ";
		if ( Manager.isFree() )
			message = "Free Edition";

		AboutDialog.showDialog(
			EditixFrame.THIS,
			EditixApplicationModel.LONG_APPNAME,
			EditixApplicationModel.getAppYear(),
			EditixApplicationModel.BUILD,
			"Alexandre Brillant",
			"images/logo.png",
			"EditiX is a cross-platform XML & XSL editor",
			message 
		);
	}

}

