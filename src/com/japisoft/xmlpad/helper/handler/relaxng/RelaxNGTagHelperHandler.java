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

package com.japisoft.xmlpad.helper.handler.relaxng;

import java.util.Enumeration;

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
		Enumeration enume = walker.getTagNodeByName( "element", true );
		FPNode node;
		
		while ( enume.hasMoreElements() ) {
			String name2 = RelaxNGToSchemaNode.getName( node = ( FPNode )enume.nextElement() );
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

