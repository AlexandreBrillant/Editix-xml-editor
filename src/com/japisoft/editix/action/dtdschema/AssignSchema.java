package com.japisoft.editix.action.dtdschema;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.action.xml.UseDefaultDialog;
import com.japisoft.editix.action.xml.UseDefaultRootBuilder;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.pathbuilder.XSDPathBuilder;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobAdapter;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.xsd.instance.XSDInstanceGenerator;
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
public class AssignSchema extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if (container == null)
			return;

		XMLPadDocument doc = container.getXMLDocument();
		boolean ok = doc.parseSchema();
		Point p = null;
		if (ok)
			p = doc.getLastDTDLocation();

		String root = container.getSchemaAccessibility().getCurrentSchemaRoot();
		String loc = container.getSchemaAccessibility().getCurrentSchema();

		if ( root == null ) {
			if ( container.getTree().getModel().getRoot() != null ) {
				root = ( ( FPNode )
					container.getTree().getModel().getRoot() ).getContent();
			}
		}

		UseDefaultDialog dialog =
			new UseDefaultDialog(
				"Schema",
				"Add a Schema declaration to the current document location",
				"Schema",
				"xsd",
				new XSDPathBuilder() );

		// Restore the last file location
		String previousPath = Preferences.getPreference( 
		"file",
		"defaultSchemaPath",
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
				
		dialog.setRoot(root);
		
		dialog.setXMLFileLocation( container.getCurrentDocumentLocation() );
		
		DelegateForRoots roots = new DelegateForRoots(); 
		
		dialog.setDelegateForRoots(
				roots );

		dialog.setVisible(true);
		if (!dialog.isOk())
			return;

		if ( dialog.getRoot() == null ) {
			EditixFactory.buildAndShowErrorDialog( "You must choose a root element" );
			return;
		}

		if ( dialog.toURILocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "You must choose a Schema location" );
			return;
		}

		previousPath = dialog.getDefaultDirectoryForLocation();

		if ( previousPath != null )
			Preferences.setPreference( "file", "defaultSchemaPath", previousPath );

		if ( container.getRootNode() == null ) {

			try {

				String sTmp = XSDInstanceGenerator.generateXMLInstance( dialog.getRoot(), dialog.toURILocation() );

				try {
					
					doc.insertString(
						container.getEditor().getCaretPosition(),
						sTmp,
						null);
					doc.parseSchema();
					ActionModel.activeActionById( "format", null);							

				} catch (BadLocationException exc ) {
				}

			} catch (Throwable e1) {
				
				ApplicationModel.debug( e1 );
				EditixFactory.buildAndShowErrorDialog( "Can't generate the XML instance : " + e1.getMessage() );
				
			}
			
		} else {

			// We update the root node
			
			FPNode rootNode = container.getRootNode();

			rootNode.removeNameSpaceDeclaration( "xsi" );
			rootNode.addNameSpaceDeclaration( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );  

			rootNode.setAttribute( "xsi:noNamespaceSchemaLocation", null );
			rootNode.setAttribute( "xsi:schemaLocation", null );			

			if ( rootNode.getNameSpaceURI() == null )
				rootNode.setAttribute( "xsi:noNamespaceSchemaLocation", dialog.toURILocation() );
			else 
				rootNode.setAttribute( "xsi:schemaLocation", rootNode.getNameSpaceURI() + " " + dialog.toURILocation() );

			doc.updateNodeOpeningClosing( rootNode );
			doc.parseSchema();			
		}

	}

	class DelegateForRoots implements UseDefaultRootBuilder {
		
		private String targetNamespace = null;
		
		public String[] getRoots(String path) {
			
			try {
				XMLFileData content = XMLToolkit.getContentFromURI( path, null );
				FPParser p = new FPParser();
				Document d = p.parse( new StringReader( content.getContent()));
				FPNode root = ( FPNode )d.getRoot();
				if ( root != null ) {
					// List all the element name
					targetNamespace = root.getAttribute( "targetNamespace" );
					ArrayList res = null;
					for ( int i = 0; i < root.getViewChildCount(); i++ ) {
						FPNode child = ( FPNode )root.getViewChildAt( i );
						if ( child.matchContent( "element" ) ) {
							if ( res == null )
								res = new ArrayList();
							res.add( child.getAttribute( "name" ) );
						}
					}
					String[] str = new String[ res.size() ];
					for ( int i = 0; i < res.size(); i++ ) 
						str[ i ] = ( String )res.get( i );
					return str;
				}
			} catch (Throwable e) {
			}

			return null;
		}
	}
}
