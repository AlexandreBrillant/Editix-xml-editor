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

public class DTDElementHandler extends AbstractHelperHandler {

	protected String getActivatorSequence() {
		return null;
	}
	
	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {	
		
		String start = "";
		String stop = "";
		
		if ( "(".equals( addedString ) ) {
			start = "(";
			stop = ")";
		} else
		if ( ",".equals( addedString ) ) {
			start = ",";
		} else
		if ( "|".equals( addedString ) ) 
			start= "|";
		
		String[] elements = document.getElementsFromDTD();
		addDescriptor( new BasicDescriptor( start + "#PCDATA" + stop ) );
		if ( elements != null ) {
			for ( int i = 0; i < elements.length; i++ ) {
				addDescriptor(
						new BasicDescriptor( start + elements[ i ] + " " + stop ) );
			}
		}
	}	

	public boolean haveDescriptors(FPNode currentNode,
			XMLPadDocument document, boolean insertBefore, int offset,
			String activatorString) {
		
		if ( activatorString == null ||
				"(".equals( activatorString ) ||
					",".equals( activatorString ) ||
						"|".equals( activatorString ) ) {
			
			// Check if inside the DTD element defintion
			if ( document.isInsideDTDElementDefinition( offset ) ) {
				return true;
			}

		}
		return false;
	}

	public String getTitle() {
		return "Element content";
	}

}
