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

package com.japisoft.editix.editor.xquery.helper;

import java.awt.Color;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class KeywordsHandler extends AbstractHelperHandler {

	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String activatorString) {

		for ( int i = 0; i < Keywords.main.length; i++ ) {

			String keyword = Keywords.main[ i ];
			BasicDescriptor rd = new BasicDescriptor( keyword );
			rd.setColor( Color.BLUE );
			addDescriptor( rd );

		}

		Color c = Color.MAGENTA.darker();
		
		for ( int i = 0; i < Keywords.axes.length; i++ ) {

			String keyword = Keywords.axes[ i ];
			BasicDescriptor rd = new BasicDescriptor( keyword );
			rd.setComment( "Axis" );
			rd.setColor( c );
			addDescriptor( rd );

		}

		c = Color.GREEN.darker();
		
		for ( int i = 0; i < Keywords.functions.length / 2; i += 2 ) {

			String keyword = Keywords.functions[ i ];
			BasicDescriptor rd = new BasicDescriptor( keyword );
			rd.setComment( "Function :\n" + Keywords.raw_functions[ i / 2 ] );
			rd.setColor( c );
			addDescriptor( rd );

		}

	}

	protected String getActivatorSequence() {
		return null;
	}

	public String getTitle() {
		return "Keywords and Functions";
	}

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		return activatorString == null;
	}

}

