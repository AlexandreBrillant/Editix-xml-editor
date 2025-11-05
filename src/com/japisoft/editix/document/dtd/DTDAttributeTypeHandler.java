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

package com.japisoft.editix.document.dtd;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class DTDAttributeTypeHandler extends AbstractHelperHandler {

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {

		if ( "#".equals( addedString ) ) {
			addDescriptor( new BasicDescriptor( addedString + "REQUIRED" ) );		
			addDescriptor( new BasicDescriptor( addedString +"IMPLIED" ) );
			addDescriptor( new BasicDescriptor( addedString + "FIXED" ) );
		} else {
			String[] types = 
				new String[] {
					"CDATA",
					"ID",
					"IDREF",
					"IDREFS",
					"NMTOKEN",
					"NMTOKENS",
					"ENTITY",
					"ENTITIES",
					"NOTATION"
			};
			for ( int i = 0; i < types.length; i++ ) {
				addDescriptor( new BasicDescriptor(
						types[ i ] ) );
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
		return 
			( "#".equals( activatorString ) || 
					activatorString == null ) && 
				document.isInsideDTDAttributeDefinition( offset );
	}

	public String getTitle() {
		return "Attribute definition";
	}

}

