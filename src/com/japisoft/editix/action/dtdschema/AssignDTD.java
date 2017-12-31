package com.japisoft.editix.action.dtdschema;

import java.awt.Point;
import java.awt.event.ActionEvent;

import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;

import com.japisoft.dtdparser.DTDParser;
import com.japisoft.dtdparser.node.DTDNode;
import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.editix.action.xml.UseDefaultDialog;
import com.japisoft.editix.action.xml.UseDefaultRootBuilder;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.pathbuilder.DTDPathBuilder;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.dtd.instance.DTDInstanceGenerator;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

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
public class AssignDTD extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if (container == null)
			return;
		XMLPadDocument doc = (XMLPadDocument) container.getEditor().getDocument();
		boolean ok = doc.parseDTD();
		Point p = null;
		if ( ok )
			p = doc.getLastDTDLocation();

		String root = container.getSchemaAccessibility().getCurrentDTDRoot();
		String loc = container.getSchemaAccessibility().getCurrentDTD();
		
		if ( root == null ) {
			if ( container.getTree().getModel().getRoot() != null ) {
				root = ( ( FPNode )
					container.getTree().getModel().getRoot() ).getContent();
			}
		}

		UseDefaultDialog dialog =
			new UseDefaultDialog(
				"DTD",
				"Assign a DTD to the current document",
				"DTD",
				"dtd",
				new DTDPathBuilder() );

		// Restore the last file location
		String previousPath = Preferences.getPreference( 
		"file",
		"defaultDTDPath",
		""
		);		

		String location = container.getCurrentDocumentLocation();
		if ( location != null ) {
			int i = location.lastIndexOf( "/" );
			if ( i == -1 )
				i = location.lastIndexOf( "\\" );
			if ( i > -1 ) {
				location = location.substring( 0, i );
				previousPath = location.substring( 0, i );
			}
		}
		
		dialog.setDefaultDirectoryForLocation( previousPath );
		
		dialog.setRoot( root );
		dialog.setXMLFileLocation( container.getCurrentDocumentLocation() );

		// List of possible root nodes
		dialog.setDelegateForRoots(
				new DelegateForRoots() );
		dialog.setVisible( true );
		if ( !dialog.isOk() )
			return;

		if ( dialog.getRoot() == null ) {
			EditixFactory.buildAndShowErrorDialog( "You must choose a root element" );
			return;
		}

		if ( dialog.toURILocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "You must choose a DTD location" );
			return;
		}
		
		previousPath = dialog.getDefaultDirectoryForLocation();
		
		if ( previousPath != null )
			Preferences.setPreference( "file", "defaultDTDPath", previousPath );
		
		String sTmp =
			"\n<!DOCTYPE " + dialog.getRoot() + " SYSTEM " + "\"" + dialog.toURILocation() + "\">\n";

		try {
			if (p != null) {
				doc.replace( p.x, p.y - p.x + 1, sTmp, null );
			} else {
				int f = doc.nextTag( 0 );
				if ( f == 0 )
					f = doc.getLength();
				doc.insertString( f, sTmp, null );
				String mustHave = "<" + dialog.getRoot();
				if ( doc.getText( 0, doc.getLength() ).indexOf( mustHave ) == -1 ) {
					
					try {
						String xml = DTDInstanceGenerator.generateXMLInstance(
								dialog.getRoot(), dialog.toURILocation(), null );
						
						// Add a root
						doc.insertString( doc.getLength(), xml, null );
						ActionModel.activeActionById( "format", null);		
						
					} catch (Throwable e1) {

						ApplicationModel.debug( e1 );
						EditixFactory.buildAndShowErrorDialog( "Can't generate the XML instance : " + e1.getMessage() );						
					
					}
				}
			}
		} catch (BadLocationException ble) {
		}
		
		doc.parseDTD();
	}

	class DelegateForRoots implements UseDefaultRootBuilder {
		public String[] getRoots(String path) {		
			// Parse it
			DTDParser parser = new DTDParser();
			try {
				String content = Toolkit.getContentFromFileName( path );
				parser.parse( new StringReader( content ) );			
				RootDTDNode node = parser.getDTDElement();
				if ( node != null ) {
					ArrayList res = null;
					for ( int i = 0; i < node.getChildCount(); i++ ) {
						DTDNode _ = node.getDTDNodeAt( i );
						if ( _.isElement() ) {
							if ( res == null )
								res = new ArrayList();
							res.add( ( ( ElementDTDNode )_).getName() );
						}
					}
					if ( res != null ) {
						String[] r = new String[ res.size() ];
						for ( int i = 0; i < res.size(); i++ )
							r[ i ] = ( String )res.get( i );
						return r;
					}
				}
			} catch (Throwable e) {
			} 
			return null;
		}
	}
}
