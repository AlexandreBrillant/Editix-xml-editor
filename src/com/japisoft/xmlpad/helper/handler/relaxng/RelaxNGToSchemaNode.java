package com.japisoft.xmlpad.helper.handler.relaxng;

import java.util.ArrayList;
import java.util.Enumeration;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AndCriteria;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.OrCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.SchemaNodeProducer;
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
public class RelaxNGToSchemaNode implements SchemaNodeProducer {

	public SchemaNode getSchemaNode(Object element) {
		FPNode e = (FPNode) element;
		SchemaNode root = new SchemaNode(SchemaNode.ROOT);

		if (e.childCount() > 0)
			processRNG(root, e);

		return root;
	}

	private void processRNG(SchemaNode newnode, FPNode node) {

		// Search for all elements or refs

		TreeWalker walker = new TreeWalker(node);
		Enumeration enume = walker.getNodeByCriteria(new OrCriteria(
				new NodeNameCriteria("element"), new NodeNameCriteria("ref")),
				true);

		// Create an OR result
		SchemaNode orNode = new SchemaNode(SchemaNode.OP_OR);
				
		while (enume.hasMoreElements()) {

			ArrayList allElements = new ArrayList();

			FPNode childNode = ( FPNode ) enume.nextElement();

			if ( childNode.matchContent( "ref" ) ) {

				processRef(new ArrayList(), childNode, allElements);

			} else {
				allElements.add(childNode);
			}

			for (int i = 0; i < allElements.size(); i++) {

				FPNode element = ( FPNode ) allElements.get( i );
				String name = getName( element );

				if ( name != null ) {
					TagDescriptor td = new TagDescriptor( name, false );
					SchemaNode node2 = new SchemaNode( td );
					processContentAndAttributes( td, element);
					orNode.addNext( node2 );
				}
			}
		}

		if ( orNode.getSchemaNodeCount() > 0 ) {
			orNode.addNext( orNode );
			newnode.addNext( orNode );
		}

	}

	void processContentAndAttributes(
			TagDescriptor element, 
			FPNode node ) {

		// Search for an empty child
		for (int i = 0; i < node.childCount(); i++) {
			FPNode foundNode = node.childAt( i );

			if ( foundNode.matchContent( "empty" ) ) {
				element.setEmpty( true );
			} else {
				if ( foundNode.matchContent( "optional" ) ) {
					if ( foundNode.childCount() > 0
							&& foundNode.childAt( 0 ).matchContent( "attribute" ) ) {
						foundNode = foundNode.childAt( 0 );
					}
				} else
					if ( foundNode.matchContent( "ref" ) ) {
						// May be some external attributes definition
						FPNode defineNode = getDefineNodeFromRefNode( foundNode );
						processContentAndAttributes( element, defineNode );
					}

				if ( foundNode.matchContent( "attribute" ) ) {
					String name = getName( foundNode );
					if ( name != null ) {
						AttDescriptor ad = new AttDescriptor( 
								name, 
								foundNode.getAttribute( "defaultValue" ), 
								!foundNode.getFPParent().matchContent( "optional"));
						element.addAttDescriptor( ad );
						
						// Enumeration case
						if ( foundNode.childCount() > 0 && 
								foundNode.childAt( 0 ).matchContent( "choice" ) ) {
							FPNode choice = foundNode.childAt( 0 );
							for ( int j = 0; j < choice.childCount(); j++ ) {
								FPNode tmpNode = choice.childAt( j );
								if ( tmpNode.matchContent( "value" ) ) {
									if ( tmpNode.childCount() > 0 ) {
										
										String val = tmpNode.childAt( 0 ).getContent(); 
										ad.addEnumValue( val );
										if ( "".equals( ad.getDefaultValue() ) )
											ad.setDefaultValue( val );

									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	static String getName(FPNode element) {
		String name = element.getAttribute("name");
		if (name != null)
			return name;
		if (element.childCount() > 0) {
			FPNode firstChild = element.childAt(0);
			if (firstChild.matchContent("name")) {
				if (firstChild.childCount() > 0)
					return firstChild.childAt(0).getContent();
			}
		}
		return null;
	}

	private FPNode getDefineNodeFromRefNode( FPNode ref ) {
		// Search for the define
		TreeWalker walker = new TreeWalker((FPNode) ref.getDocument()
				.getRoot());

		String refName = getName(ref);
		if (refName == null)
			return null;

		FPNode defineNode = walker.getOneNodeByCriteria(new AndCriteria(
				new NodeNameCriteria("define"), new AttributeCriteria("name",
						refName)), false);
		return defineNode;
	}
	
	private void processRef(ArrayList defineUsed, FPNode ref,
			ArrayList elementFound) {

		FPNode defineNode = getDefineNodeFromRefNode( ref );
		if ( defineNode == null )
			return;

		if (!defineUsed.contains(defineNode)) {
			defineUsed.add(defineNode);

			// Process again

			TreeWalker walker2 = new TreeWalker( defineNode );
			Enumeration enume = walker2.getNodeByCriteria( new OrCriteria(
					new NodeNameCriteria( "element" ),
					new NodeNameCriteria( "ref" ) ), true );

			while (enume.hasMoreElements()) {

				FPNode node = (FPNode) enume.nextElement();

				// Get it only if a parent is not another element

				boolean getit = true;

				FPNode tstNode = node.getFPParent();
				while (tstNode != null) {
					if (tstNode.matchContent( "element" ) ) {
						getit = false;
						break;
					} else if (tstNode.matchContent( "define" ) )
						break;
					tstNode = tstNode.getFPParent();
				}

				if ( getit ) {
					if ( node.matchContent( "element" ) ) {
						elementFound.add( node );
					} else
						processRef( defineUsed, node, elementFound );
				}

			}
		}
	}

}
