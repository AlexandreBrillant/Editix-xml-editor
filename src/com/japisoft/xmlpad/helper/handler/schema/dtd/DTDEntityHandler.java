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

package com.japisoft.xmlpad.helper.handler.schema.dtd;

import com.japisoft.framework.xml.dtdparser.node.DTDNode;
import com.japisoft.framework.xml.dtdparser.node.EntityDTDNode;
import com.japisoft.framework.xml.dtdparser.node.RootDTDNode;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.schema.AbstractEntityHandler;
import com.japisoft.xmlpad.helper.model.EntityDescriptor;

/** Handler for DTD entities */
public class DTDEntityHandler extends AbstractEntityHandler {

	private RootDTDNode root;

	public DTDEntityHandler(  
			RootDTDNode root ) {
		this.root = root;
	}

	public void dispose() {
		super.dispose();
		this.root = null;
	}	

	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset, 
			String addedString ) {
		for ( int i = 0; i < root.getDTDNodeCount(); i++ ) {
			DTDNode node = root.getDTDNodeAt( i );
			if ( node instanceof EntityDTDNode ) {
				EntityDTDNode entity = ( EntityDTDNode )node;
				EntityDescriptor ed = new EntityDescriptor(
						addedString +
						entity.getName(),
						entity.getValue() );
				ed.setComment(
						entity.getNodeComment() );
				addDescriptor( ed );
			}
		}
	}	

}
