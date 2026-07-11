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

package com.japisoft.xmlpad.helper.handler.relaxng;

import java.util.Enumeration;
import java.util.Iterator;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;
import com.japisoft.xmlpad.helper.model.SchemaNodable;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

/** Handler for relaxng */
public class RelaxNGTagHelperHandler extends AbstractTagHandler implements
		SchemaNodable {

	private FPNode root;

	public RelaxNGTagHelperHandler( FPNode root ) {
		this.root = root;
	}

	public void dispose() {
		super.dispose();
		this.root = null;
	}	

	public TagDescriptor getTag( FPNode node ) {
		FPNode xmlnode = getXMLElementByName( node.getContent() );
		if ( xmlnode != null ) {
			
			RelaxNGToSchemaNode rng = new RelaxNGToSchemaNode();			
			TagDescriptor td = new TagDescriptor( node.getContent(), false );
			rng.processContentAndAttributes( td, xmlnode );
			
			return td;
			
		}
		return null;
	}

	protected void notifyLocation() {
		schemaNode = null;
		
		if ( currentDocumentNode == null ) {
			schemaNode = ( new RelaxNGToSchemaNode() ).getSchemaNode( root );
		} else {
			FPNode element = getXMLElementByName( currentDocumentNode.getContent() );		
			if ( element != null ) {
				schemaNode = ( new RelaxNGToSchemaNode() ).getSchemaNode( element );
			}
		}
	}

	private FPNode getXMLElementByName( String name ) {
		TreeWalker walker = new TreeWalker( root );
		Iterator enume = walker.getTagNodeByName( "element", true );
		FPNode node;
		
		while ( enume.hasNext() ) {
			String name2 = RelaxNGToSchemaNode.getName( node = ( FPNode )enume.next() );
			if ( name.equals( name2 ) )
				return node;
		}

		return null;
	}

	private SchemaNode schemaNode = null;

	public SchemaNode getSchemaNode() {
		return schemaNode;
	}

	public SchemaNode getSecondarySchemaNode() {
		return null;
	}	

}
