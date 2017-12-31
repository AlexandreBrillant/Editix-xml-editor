package com.japisoft.xmlpad.helper.handler.schema.dtd;

import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;
import com.japisoft.xmlpad.helper.model.SchemaNodable;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
