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

package com.japisoft.xmlform.editor.actions.file;

import java.awt.event.ActionEvent;
import java.io.File;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.editor.actions.CommonAction;

public class NewAction extends CommonAction {

	@Override
	public void actionPerformed2(ActionEvent e) {

		File f = FileManager.getSelectedFile( 
				true, 
				"xf", 
				"XML form description" );
		if ( f != null ) {
			try {
				frame.newDocument( f );
			} catch (Exception e1) {
				UIToolkit.dispatchError( "Can't create a new document : " + e1.getMessage() );
				ApplicationModel.debug( e1 );
			}
		}
	}

}

