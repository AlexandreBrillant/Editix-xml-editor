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

package com.japisoft.xmlpad.toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.file.LoadAction;

/**
 * <p>
 * This tookit is a manager for single XML file edition. You can invoke
 * this class each time you need to edit a XML file. This is
 * for instance an XML configuration file for your application.</p>
 * <p>
 * Here a sample of usage : 
 * SingleDocumentEditor.edit( "c:/tmp/myFile.xml", SingleDocumentEditor.NEW_DOCUMENT );
 * This line will create a new 'myFile.xml' document. If you have existing myFile.xml document, use
 * SingleDocumentEditor.CURRENT_DOCUMENT for the last argument.
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
public class SingleDocumentEditor extends JFrame {
	private XMLContainer container;

	public final static boolean NEW_DOCUMENT = true;
	public final static boolean CURRENT_DOCUMENT = !NEW_DOCUMENT;

	private SingleDocumentEditor() {
		super( "Single editor" );
		getContentPane().add( ( container = new XMLContainer() ).getView() );
		setSize( 550, 400 );
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

	/** @return the current XMLContainer */
	public XMLContainer getXMLContainer() { return container; }

	/** Edit the provided file by opening a new Frame with the XMLPad container.
	 * @param fileName The XML document to edit
	 * @param newOne true if the file can be created or false for an existing one
	 * @return The new SingleDocumentEditor
	 * @throws FileNotFoundException If the file cannot be found
	 * @throws IOException if the document cannot be loaded
	 */
	public static SingleDocumentEditor showEditor( String fileName, boolean newOne ) throws FileNotFoundException, IOException  {
		SingleDocumentEditor sde = new SingleDocumentEditor();
		sde.setTitle( fileName );
		if ( !newOne || new File( fileName ).exists() ) {
			try {
				LoadAction.loadInBuffer( sde.container, fileName );
			} catch( Throwable th ) {
				if ( th instanceof FileNotFoundException )
					throw (FileNotFoundException)th;
				else
					throw new IOException( th.getMessage() );
			}
		}
		else
			sde.container.getDocumentInfo().setCurrentDocumentLocation( fileName );
		sde.setVisible( true );
		return sde;
	}

	public static void main( String[] args ) throws Throwable {
		SingleDocumentEditor.showEditor( "/home/japisoft/phoneBook/project/toto.xml", false );
	}
}

