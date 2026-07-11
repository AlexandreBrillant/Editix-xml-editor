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

package com.japisoft.editix.action.xml.xinclude;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.DOMToolkit;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class ExtractNodeAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}
		FPNode node = container.getCurrentElementNode();
		if ( node == null ) {
			EditixFactory.buildAndShowErrorDialog( "Please select a node before" );
			return;
		}
		if ( node.isRoot() ) {
			EditixFactory.buildAndShowErrorDialog( "Can't extract the root node, use only descendant elements" );
			return;
		}
		
		File f = FileManager.getSelectedFile( true, "xml", "XML file", container.getCurrentDocumentLocation() );
		if ( f != null ) {
			String xpath = node.getXPathLocation();
			Node n = null;
			try {
				n = XMLToolkit.extractNodeFromXPath( container.getText(), xpath );
			} catch( Exception exc ) {
			}
			if ( n == null ) {
				EditixFactory.buildAndShowErrorDialog( "Can't extract this node ?" );
				return;
			}
			try {
				String newFileContent = XMLToolkit.nodeToText( n );
				XMLToolkit.save( f, newFileContent );
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't extract or save this node : " + exc.getMessage() );
				return;
			}
			
			Document d = n.getOwnerDocument();
			Element include = d.createElementNS( XMLToolkit.NS_XINCLUDE, "xs:include" );
			include.setAttribute( "href", com.japisoft.framework.app.toolkit.Toolkit.getRelativePath( f, new File( container.getCurrentDocumentLocation() ), true ) );
			n.getParentNode().replaceChild( include, n );
			try {
				container.setText( DOMToolkit.DOM2String( d , 1, true ) );
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't set the new include node :" + exc.getMessage() );
			}
			
		}
		
	}

}
