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

package com.japisoft.editix.wizard.document;

import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.xml.xsd.instance.XSDInstanceGenerator;

public class XSD2XMLDocument implements DocumentWizard {

	private File source;
	
	@Override
	public String start() {
		JFileChooser fc = EditixFactory.buildFileChooser( new FileFilter() {			
			@Override
			public String getDescription() {
				return "W3C Schema file (*.xsd)";
			}
			@Override
			public boolean accept( File f ) {
				if ( f.isFile() ) {
					String tmp = f.getName().toLowerCase();
					return tmp.endsWith( ".xsd" );
				} else
					return true;
			}
		});
		if ( fc.showOpenDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {
			try {
				source = fc.getSelectedFile();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware( true );
				DocumentBuilder builder = factory.newDocumentBuilder();
				
				Document doc = builder.parse( source );
				NodeList nl = doc.getDocumentElement().getChildNodes();
				JComboBox<String> combo = new JComboBox<String>();

				for ( int i = 0; i < nl.getLength(); i++ ) {
					if ( nl.item( i ) instanceof Element ) {
						Element e = ( Element )nl.item( i );
						if ( "element".equals( e.getNodeName() ) || "element".equals( e.getLocalName() ) )
							combo.addItem( e.getAttribute( "name" ) );
					}
				}
				
				if ( combo.getItemCount() == 0 ) {
					EditixFactory.buildAndShowWarningDialog( "Can't find element definition inside your schema ?" );
					return null;
				}

				if ( DialogManager.showDialog( 
						EditixFrame.THIS, 
						"Select the root node", 
						"Selection the root node for generating your XML document", 
						"", 
						null, 
						combo ) == DialogManager.OK_ID ) {
					String root = ( String )combo.getSelectedItem();
					if ( root == null ) {
						EditixFactory.buildAndShowErrorDialog( "No root selection ?" );
						return null;
					}
					
					return "<?xml version='1.0'?>\n\n" + XSDInstanceGenerator.generateXMLInstance( root, source.toString() );

				}

			} catch( Throwable exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't use this file : " + exc.getMessage() );
			}
			
		}
		return null;
	}
	
	public File getSource() { return source; }
	
}

