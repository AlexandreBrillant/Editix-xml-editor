package com.japisoft.framework.xml.refactor.elements;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

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
public class NamespaceRefactor extends AbstractRefactor {
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";
	private static final String DELETE_ACTION = "DELETE (V1)";

	public static String[] ACTIONS = new String[] {
		RENAME_ACTION,
		DELETE_ACTION,
	};

	public NamespaceRefactor() {
		super( ANY );
	}

        public String[] getActions() {
            return ACTIONS;
        }

        public String getName() {
            return "Namespace";
        }

	protected Node refactorIt( Node node, RefactorAction ra ) {

		if ( RENAME_ACTION.equals( ra.getAction() ) ) {

			if ( node.getNodeType() == Node.ATTRIBUTE_NODE ) {
				
				if ( node.getNodeName().startsWith( "xmlns:" ) || node.getNodeName().equals( "xmlns" ) ) {
					
					if ( ra.matchOldValue( node.getNodeValue() ) ) {
						
						if ( !ra.isNewValueEmpty() )
							
							node.setNodeValue( ra.getNewValue() );
						
					}

				}
				
			}
			
		} else
		if ( DELETE_ACTION.equals( ra.getAction() ) ) {

			if ( node.getNodeType() == Node.ATTRIBUTE_NODE ) {
			
				if ( node.getNodeName().equals( "xmlns" ) ) {
	
					if ( ra.matchOldValue( node.getNodeValue() ) )
						return null;
					
				} 
			
			}

			if ( node.getNodeType() == Node.ELEMENT_NODE ) {

				// Check for attributs
				
				NamedNodeMap map = node.getAttributes();
				if ( map != null ) {
					for ( int i = 0; i < map.getLength(); i++ ) {
						
						Node attNode = map.item( i );
						
						if ( attNode.getNodeName().startsWith( "xmlns:" ) ) {
							
							if ( ra.matchOldValue( attNode.getNodeValue() ) ) {

								( ( Element )node ).setAttribute( attNode.getNodeName(), null );
								map.removeNamedItem( attNode.getNodeName() );
								
								// Prefix name

								int j = attNode.getNodeName().indexOf( ":" );
								String prefix = attNode.getNodeName().substring( j + 1 );

								if ( prefix.equals( node.getPrefix() ) ) {
									// Remove it
									node.setPrefix( null );
								}
									
								// Check for descendant
								Element e = ( Element )node;
								NodeList nl = e.getElementsByTagName( "*" );

								for ( int k = 0; k < nl.getLength(); k++ ) {
									Node n = ( Node )nl.item( k );
									if ( prefix.equals( n.getPrefix() ) ) {
										n.setPrefix( null );
									}
									cleanAttributesWithPrefix( n, prefix );
								}
								
							}
							
						}
						
					}
				}
				
			}
						
		}

		return node;
	}

	private void cleanAttributesWithPrefix( Node n, String prefix ) {

		NamedNodeMap nnm = n.getAttributes();
		for ( int i = 0; i < nnm.getLength(); i++ ) {
			
			Node n2 = nnm.item( i );
			if ( n2.getNodeName().startsWith( prefix + ":" ) ) {

				String value = n2.getNodeValue();
				nnm.removeNamedItem( n2.getNodeName() );
				( ( Element )n ).setAttribute( n2.getNodeName().substring( prefix.length() + 1 ), value ); 

			}
			
		}
		
	}

	public void initTable( RefactorTable table, FPNode context ) {
		if ( context.hasAttribute( "xmlns" ) ) {
			table.init( 0, context.getAttribute( "xmlns" ) );
		}
		if ( context.getNameSpaceURI() != null ) {
			table.init( 0, context.getNameSpaceURI() );
		}
	}	

}
