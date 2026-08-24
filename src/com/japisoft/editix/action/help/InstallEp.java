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

package com.japisoft.editix.action.help;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.japisoft.editix.ep.EPManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.FileManager;

public class InstallEp extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		File f = FileManager.getSelectedFile( true, "zip", "Editix Extension Pack file" );
		if ( f != null ) {
			try {
				EPManager.instance().install( f );
				EditixFactory.buildAndShowInformationDialog( "Please restart Editix to apply the changes." );
			} catch( Exception exc ) {
				exc.printStackTrace();
				EditixFactory.buildAndShowErrorDialog( "Can't install this pack : [" + exc.getMessage() + "]" );
			}
		}
	}

}