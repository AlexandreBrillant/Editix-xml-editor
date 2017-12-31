package com.japisoft.xmlpad.toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.file.LoadAction;
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
