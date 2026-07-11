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

package com.japisoft.editix.action.fop;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.FontInfo;

public class FOConfigAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent arg0) {	
		if ( storeConfigurationFile() )
			OpenAction.openFile( "XML", false, EditixFOPFactory.fopXML, "UTF-8" );
	}

	static boolean storeConfigurationFile() {
		if ( !EditixFOPFactory.fopXML.exists() ) {
			try {
				BufferedWriter bw = new BufferedWriter( 
					new OutputStreamWriter( 
							new FileOutputStream( EditixFOPFactory.fopXML ), "UTF-8" ) );
				try {
					bw.write( "<?xml version=\"1.0\"?>" );bw.newLine();
					bw.write( "<fop version=\"1.0\">" );bw.newLine();
					bw.write( "	<strict-configuration>true</strict-configuration>" );bw.newLine();
					bw.write( "	<!-- Strict FO validation -->");bw.newLine();
					bw.write( "	<strict-validation>true</strict-validation>");bw.newLine();
					bw.write( "	<!-- Base URL for resolving relative URLs -->" );bw.newLine();
					bw.write( "	<base>./</base>" );bw.newLine();
					bw.write( "	<!-- Font Base URL for resolving relative font URLs -->" );bw.newLine();
					bw.write( "	<font-base>./</font-base>" );bw.newLine();
					bw.write( "	<!-- Source resolution in dpi (dots/pixels per inch) for determining the size of pixels in SVG and bitmap images, default: 72dpi -->" );bw.newLine();
					bw.write( "	<source-resolution>72</source-resolution>" );bw.newLine();
					bw.write( "	<!-- Target resolution in dpi (dots/pixels per inch) for specifying the target resolution for generated bitmaps, default: 72dpi -->" );bw.newLine();
					bw.write( "	<target-resolution>72</target-resolution>" );bw.newLine();
					bw.write( "	<renderers>" );bw.newLine();
					bw.write( " 	<renderer mime=\"application/pdf\">");bw.newLine();
					bw.write( " 		<fonts>" );bw.newLine();
					bw.write( " 			<directory recursive=\"true\">" + FontInfo.getDefaultFontDirectory() + "</directory>" );bw.newLine();
					bw.write( " 			<auto-detect/>" );bw.newLine();
					bw.write( " 		</fonts>" );bw.newLine();
					bw.write( " 	</renderer>" );bw.newLine();
					bw.write( "	</renderers>" );bw.newLine();
					bw.write( "</fop>" );bw.newLine();
				} finally {
					bw.close();
				}
			} catch( Exception exc ) {
				return false;
			}
		}
		return true;		
	}

}
