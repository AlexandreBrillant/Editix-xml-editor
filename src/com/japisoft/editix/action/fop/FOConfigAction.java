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

package com.japisoft.editix.action.fop;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.ui.EditixFactory;

public class FOConfigAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if ( !EditixFOPFactory.fopXML.exists() ) {
			try {
				BufferedWriter bw = new BufferedWriter( 
					new OutputStreamWriter( 
							new FileOutputStream( EditixFOPFactory.fopXML ), "UTF-8" ) );
				try {
					bw.write( "<?xml version=\"1.0\"?>" );bw.newLine();
					bw.write( "<fop version=\"1.0\">" );bw.newLine();
					bw.write( " <!-- Strict user configuration -->");bw.newLine();
					bw.write( " <strict-configuration>true</strict-configuration>" );bw.newLine();
					bw.write( " <!-- Strict FO validation -->");bw.newLine();
					bw.write( " <strict-validation>true</strict-validation>");bw.newLine();
					bw.write( " <!-- Base URL for resolving relative URLs -->" );bw.newLine();
					bw.write( " <base>./</base>" );bw.newLine();
					bw.write( " <!-- Font Base URL for resolving relative font URLs -->" );bw.newLine();
					bw.write( " <font-base>./</font-base>" );bw.newLine();
					bw.write( " <!-- Source resolution in dpi (dots/pixels per inch) for determining the size of pixels in SVG and bitmap images, default: 72dpi -->" );bw.newLine();
					bw.write( " <source-resolution>72</source-resolution>" );bw.newLine();
					bw.write( " <!-- Target resolution in dpi (dots/pixels per inch) for specifying the target resolution for generated bitmaps, default: 72dpi -->" );bw.newLine();
					bw.write( "  <target-resolution>72</target-resolution>" );bw.newLine();
					bw.write( "</fop>" );bw.newLine();
				} finally {
					bw.close();
				}
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't write default fop.xml : " + exc.getMessage() );
				return;
			}
		}
		OpenAction.openFile( "XML", false, EditixFOPFactory.fopXML, "UTF-8" );
	}

}

