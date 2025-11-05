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

package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;

import DTDDoc.DTDCommenter;
import DTDDoc.ExtendedDTD;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.XMLContainer;

public class GenerateDTDDoc extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		if ( Manager.isFree() ) {

			EditixFactory.buildAndShowInformationDialog( "This action is not available inside the Free Edition.\nPlease look at http://www.editix.com" );
			BrowserCaller.displayURL( "http://www.editix.com" );
			return;

		}	

		//���
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container.getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "Please save your DTD before" );
			return;
		}

		File f = FileManager.getSelectedFile( false,
				"html", "HTML output file" );
		if ( f != null ) {
			try {

				ExtendedDTD dtd = new ExtendedDTD( new File( container.getCurrentDocumentLocation() ) );
				
				String header = GenerateSchemaDoc.getCommonHeader( container );
				String footer = GenerateSchemaDoc.getCommonFooter( container );
				
				DTDCommenter c = new DTDCommenter( header, footer );
				c.createDocumentation(
						dtd, 
						new PrintWriter( new FileWriter( f ) ), true );

				BrowserCaller.displayURL( f.toString() );
							
			} catch (IOException e1) {
				EditixFactory.buildAndShowErrorDialog( "Can't create documentation : " + e1.getMessage() );
			}
			
		}
		//��
	}

}

