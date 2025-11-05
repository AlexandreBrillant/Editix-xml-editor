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
import java.io.FileWriter;

import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.editor.EditorModel;
import com.japisoft.xmlform.editor.actions.CommonAction;

public class SaveAction extends CommonAction {

	public void actionPerformed2(ActionEvent e) {
		if ( EditorModel.CURRENT_DOCUMENT == null ) {
			File f = FileManager.getSelectedFile( 
					true,
					"xml",
					"XML Document" );
			EditorModel.CURRENT_DOCUMENT = f.toString();
		}
		if ( EditorModel.CURRENT_DOCUMENT != null )
			saveTo( EditorModel.CURRENT_DOCUMENT );		
	}

	public void saveTo( String uri ) {
		try {
			frame.saveDocument( uri );
		} catch ( Exception e ) {
			UIToolkit.dispatchError( Traductor.traduce( "cantsave", "Can't save" ) + " : " + e.getMessage() );
		}
	}

	public static void main( String[] args ) throws Exception {
		
		System.out.println( "Test �criture" );
		
		String p = "\\\\d-siteorsys-xp\\cours-xml\\ACE.xml";
		
		FileWriter fw = new FileWriter( new File( p ) );
		try {
			fw.write( "TEST" );
		} finally {
			fw.close();
		}
		
	}
	
}

