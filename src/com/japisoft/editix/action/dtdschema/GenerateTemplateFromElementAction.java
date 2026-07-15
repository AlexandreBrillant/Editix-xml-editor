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

package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.dtd.instance.DTDInstanceGenerator;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.xsd.instance.XSDInstanceGenerator;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

/* Generate template from the current XML element */
public class GenerateTemplateFromElementAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();

		if ( container.getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowWarningDialog( "Please save your dtd/schema before generating a new XML document" );
			return;
		}

		String type = container.getDocumentInfo().getType();

		if ( "DTD".equals( type ) ) {

			XMLPadDocument doc = container.getXMLDocument();
			String root = doc.getDTDElementDefinitionFor( container.getCaretPosition() );
			if ( root == null ) {
				EditixFactory.buildAndShowErrorDialog( "Select a line with an element definition" );
			} else {
				
				try {

					generateXMLDocumentFromDTD( root, container.getCurrentDocumentLocation() );					

				} catch ( Throwable e1 ) {

					EditixApplicationModel.debug( e1 );
					EditixFactory.buildAndShowErrorDialog( "Can't build this XML document" );

				}

			}
		} else
			if ( "XSD".equals( type ) ) {
				
				FPNode node = container.getCurrentElementNode();
						
				try {

					generateXMLDocumentFromXSD( node, container.getCurrentDocumentLocation() );
					
				} catch ( Throwable e1 ) {

					EditixApplicationModel.debug( e1 );
					EditixFactory.buildAndShowErrorDialog( "Can't build this XML document" );

				}

			}		
	}

	static void generateXMLDocumentFromDTD( String root, String location ) throws Throwable {

		String xml = DTDInstanceGenerator.generateXMLInstance(
				root, location, null );
		IXMLPanel panel = EditixFactory.buildNewContainer();

		xml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
			"\n<!DOCTYPE " + root + " SYSTEM " + "\"" + location + "\">\n" +
				xml;

		panel.getMainContainer().setText( xml );

		FormatAction.format( 
				panel.getMainContainer(), 
				null, 
				null, 
				"silence" );					

		EditixFrame.THIS.addContainer( panel );
		
	}
	
	static void generateXMLDocumentFromXSD( FPNode node, String location ) throws Throwable {

		String name = null;

		while ( node != null && ( name == null ) ) {
		
			if ( node.matchContent( "element"  ) ) {
				name = node.getAttribute( "name" );
				if ( name == null )
					name = node.getAttribute( "ref" );
			}
			
			node = node.getFPParent();
			
		}

		if ( name == null || "".equals( name ) ) {
			throw new Exception( "Can't find a name in this element definition ?" );	
		}

		String sTmp = XSDInstanceGenerator.generateXMLInstance( 
				name, 
				location
		);

		FPNode rootNode = ( FPNode )node.getDocument().getRoot();
		String target = null;

		if ( rootNode.hasAttribute( "targetNamespace" ) )
			target = rootNode.getAttribute( "targetNamespace" );
		
		int i = sTmp.indexOf( "<" + name );
		if ( i > -1 ) {
			
			sTmp = sTmp.substring( 0, i + 1 + name.length() ) + sTmp.substring( i + 1 + name.length() );

		}

		String xml = "<?xml version='1.0' encoding='UTF-8'?>\n";
		xml += sTmp;

		IXMLPanel panel = EditixFactory.buildNewContainer();
		panel.getMainContainer().setText( xml );

		FormatAction.format( 
				panel.getMainContainer(), 
				null, 
				null, 
				"silence" );					

		EditixFrame.THIS.addContainer( panel );

	}
	
}
