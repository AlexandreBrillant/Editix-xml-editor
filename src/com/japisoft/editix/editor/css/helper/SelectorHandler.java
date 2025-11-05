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

package com.japisoft.editix.editor.css.helper;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;

import com.japisoft.dtdparser.DTDParser;
import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class SelectorHandler extends AbstractHelperHandler {

	private RootDTDNode node = null;
	private boolean rejectIt = false;

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {

		// Parse the HTML DTD
		XMLDocumentInfo info = 
			DocumentModel.getDocumentForType( "XHTML" );
		String HTMLDTD = info.getDefaultDTDLocation();
		if ( HTMLDTD.startsWith( "file:" ) )
			HTMLDTD = HTMLDTD.substring( 5 );
		if ( HTMLDTD.indexOf( ":" ) > -1 ) {
			if ( HTMLDTD.startsWith( "/" ) )
				HTMLDTD = HTMLDTD.substring( 1 );
		}

		DTDParser parser = new DTDParser();
		try {
			parser.parse( new FileInputStream( HTMLDTD ) );
			node = parser.getDTDElement();
		} catch (IOException e) {
			rejectIt = true;
		}
		
		if ( node != null ) {
				for ( int i = 0; i < node.getDTDNodeCount(); i++ ) {
					Color pink2 = Color.PINK.darker();
					if ( node.getDTDNodeAt( i ).isElement() ) {
						addDescriptor( new BasicDescriptor( ( ( ElementDTDNode )node.getDTDNodeAt( i ) ).getName() ) );						
					}
				}
		}
	}

	protected String getActivatorSequence() {
		return null;
	}
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		return activatorString == null && !PropertiesHandler.inSelector( document, offset );
	}

	public String getTitle() {
		return "Selector";
	}

	public int getPriority() {
		return -1;
	}

}

