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
