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

import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;
import com.japisoft.xmlpad.helper.model.SchemaNodable;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

/** Handler for DTD tag */
public class DTDTagHandler extends AbstractTagHandler 
		implements SchemaNodable {
	private RootDTDNode root;
	private String rootElement;

	public DTDTagHandler( 
			String rootElement, 
			RootDTDNode root ) {
		this.root = root;
		this.rootElement = rootElement;
	}

	public void dispose() {
		super.dispose();
		this.root = null;
	}	

	public TagDescriptor getTag( FPNode node ) {
		if ( node == null )
			return null;
		String name = node.getContent();
		ElementDTDNode element = root.getElementDeclaration( name );
		if ( element != null )
			return new DTDTagDescriptor( element );
		return null;
	}

	protected void notifyLocation() {

		if ( root == null )
			return;

		schemaNode = null;
		
		if ( currentDocumentNode == null ) {
			// Add the rootElement

			if ( rootElement != null ) {
				ElementDTDNode node = root.getElementDeclaration( rootElement );
				if ( node != null ) {
					addTagDescriptor( new DTDTagDescriptor( node ) );
				}
			}

			return;
		}

		String name = currentDocumentNode.getContent();
		
		// Search for the DTD declaration

		ElementDTDNode node = root.getElementDeclaration( name );

		if ( node == null || 
				node.isEmptyElement() || 
					node.hasPCDATA() )
			return;

		schemaNode = ( new DTDToSchemaNode() ).getSchemaNode( node );
	}

	protected void completeContentForElementWithoutPrefix() {
		super.completeContentForElementWithoutPrefix();
		if ( rootElement != null && rootElement.indexOf( ":" ) > 0 ) {
			for ( int i = 0; i < root.getDTDNodeCount(); i++ ) {
				if ( root.getDTDNodeAt( i ).isElement() ) {
					ElementDTDNode node = (ElementDTDNode)root.getDTDNodeAt( i );
					if ( node.getName().indexOf( ":" ) == -1 )
						addTagDescriptor( new DTDTagDescriptor( node ) );
				}
			}
		}
	}
	
	private SchemaNode schemaNode = null;

	public SchemaNode getSchemaNode() {
		return schemaNode;
	}

}
