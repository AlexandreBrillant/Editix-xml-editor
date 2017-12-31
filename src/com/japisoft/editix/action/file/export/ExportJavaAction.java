package com.japisoft.editix.action.file.export;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.XMLContainer;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class ExportJavaAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
	
		
		//£££		
		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();

		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}

		if ( container.getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "Your must save your document" );
			return;			
		}

		JFileChooser f = new JFileChooser();
		f.setMultiSelectionEnabled( false );
		
		f.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		if ( f.showSaveDialog( EditixFrame.THIS ) == 
			JFileChooser.APPROVE_OPTION ) {

			String pack = EditixFactory.buildAndShowInputDialog( "Java package ?" );
			if ( pack == null )
				pack = "editix";
			
			try {
				ClassGenerator
					cg = new ClassGenerator(
							container.getCurrentDocumentLocation(),
							f.getSelectedFile().toString(),
							pack
					);

				// Copy the parser too
				copyJavaFile( "javatemplate/AbstractElement.javat",f.getSelectedFile(),pack );
				copyJavaFile( "javatemplate/ObjectBuilder.javat",f.getSelectedFile(),pack );
				copyJavaFile( "javatemplate/SaxHandler.javat",f.getSelectedFile(),pack );
				copyJavaFile( "javatemplate/Tools.javat",f.getSelectedFile(),pack );

			} catch (Throwable e1) {

				EditixFactory.buildAndShowErrorDialog( 
						"Can't create classes : " + e1.getMessage() );

			}

		}
		//££
	}

	private void copyJavaFile( String name, File dirDest, String newpackage ) {
		URL u = ClassLoader.getSystemClassLoader().getResource( name );
		if ( u == null ) {
			ApplicationModel.debug("Can't write java class " + name );
		} else {
			try {
				InputStream input = u.openStream();
				InputStreamReader reader = new InputStreamReader( input );
				BufferedReader br = new BufferedReader( reader );
				try {
					// Skip the package
					br.readLine();
					
					int i = name.indexOf( "/" );
					name = name.substring( i + 1 );
					
					BufferedWriter bw 
						= new BufferedWriter( 
								new FileWriter( 
										new File( 
												dirDest, 
												name.substring( 0, name.length() - 1 ) ) ) );
					try {
						if ( newpackage != null ) {
							bw.write( "package " + newpackage + ";" );
							bw.newLine();
						}
						String line = null;
						while ( ( line = br.readLine() ) != null ) {
							bw.write( line );
							bw.newLine();
						}
					} finally {
						bw.close();
					}
				} finally {
					br.close();
				}
			} catch (IOException e) {
			}
			
		}
	}

}
