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

package com.japisoft.xmlpad.helper.handler.system;

import java.util.List;

import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.model.AttDescriptor;

public class DefaultAttributeHandler extends AbstractSystemHandler {

	@Override
	protected void installDescriptors(FPNode currentNode,
			XMLPadDocument document, int offset, String activatorString) {
		if ( currentNode != null ) {
			String tag = currentNode.getNodeContent();
			Document d = currentNode.getDocument();
			if ( d != null ) {
				List<String> l = d.getOrderedAttributes( tag );
				for ( int i = 0; i < l.size(); i++ ) {
					addDescriptor( new AttDescriptor( l.get( i ), null ) );
				}
			}
		}							
	}
	
	@Override
	protected String getActivatorSequence() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document,
			boolean insertBefore,
			int offset,
			String activatorString ) {
		if ( null == activatorString ) {
			if ( !document.hasSchema() ) {
				if ( document.isInsideTag( offset, true, true ) ) {
					return true;
				}
			}			
		}
		return false;
	}
	
}
