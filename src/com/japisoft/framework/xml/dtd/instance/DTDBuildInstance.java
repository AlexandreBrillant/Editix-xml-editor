package com.japisoft.framework.xml.dtd.instance;

import com.japisoft.dtdparser.node.AttributeDTDNode;
import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.ElementRefDTDNode;
import com.japisoft.dtdparser.node.ElementSetDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.dtdparser.node.DTDNode;

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
public class DTDBuildInstance {

	public void buildElement( StringBuffer res, String elementName, RootDTDNode root ) {
		for ( int i = 0; i < root.getChildCount(); i++ ) {
			DTDNode node = root.getDTDNodeAt( i );
			if ( node.isElement() ) {
				ElementDTDNode element = ( ElementDTDNode )node;
				if ( elementName.equals( element.getName() ) ) {
					buildElement( res, element, root );
					break;
				}
			}
		}
	}

	private void buildNode( StringBuffer res, DTDNode node, RootDTDNode root ) {
		if ( node.isElement() )
			buildElement( res, ( ElementDTDNode )node, root );
		else
		if ( node.isElementRef() ) {
			ElementRefDTDNode ref = ( ElementRefDTDNode )node;
			if ( ref.hasPCDATA() ) {
				res.append( "VALUE" );
			} else {
				if ( ref.getOperator() == ElementDTDNode.ZERO_MORE_ITEM_OPERATOR )
					return;
				if ( ref.getOperator() == ElementDTDNode.ZERO_ONE_ITEM_OPERATOR )
					return;
				if ( ref.getReferenceNode() != null )
					buildElement( res, ref.getReferenceNode(), root );
			}
		} else
		if ( node.isElementSet() )
			buildElementSet( res, ( ElementSetDTDNode )node, root );
	}

	private void buildElement( StringBuffer res, ElementDTDNode element, RootDTDNode root ) {
		if ( element.getOperator() == ElementDTDNode.ZERO_MORE_ITEM_OPERATOR )
			return;
		if ( element.getOperator() == ElementDTDNode.ZERO_ONE_ITEM_OPERATOR )
			return;
		
		res.append( "<" ).append( element.getName() );
		for ( int i = 0; i < element.getChildCount(); i++ ) {
			DTDNode node = element.getDTDNodeAt( i );
			if ( node.isAttribute() ) {
				AttributeDTDNode att = ( AttributeDTDNode )node;
				buildAttribute( res, att );
			}
		}
		// Look at the content
		if ( element.isEmpty() ) {
			res.append( "/>" );
		} else {
			res.append( ">" );

			for ( int i = 0; i < element.getChildCount(); i++ ) {
				DTDNode node = element.getDTDNodeAt( i );
				if ( node.isElement() ) {					
					ElementDTDNode e = ( ElementDTDNode )node;
					if ( e.hasPCDATA() ) {
						res.append( "VALUE" );
					}

					if ( e.getOperator() == 
						ElementDTDNode.ZERO_MORE_ITEM_OPERATOR ||
							e.getOperator() ==
								ElementDTDNode.ZERO_ONE_ITEM_OPERATOR )
						continue;
					buildElement( res, e, root );
				} else
					buildNode( res, node, root );
			}

			res.append( "</" ).append( element.getName() ).append( ">" );
		}
	}

	private void buildElementSet( StringBuffer res, ElementSetDTDNode set, RootDTDNode root ) {
		if ( set.getType() == ElementSetDTDNode.SEQUENCE_TYPE ) {
			for ( int i = 0; i < set.getChildCount(); i++ ) {
				buildNode( res, set.getDTDNodeAt( i ), root );
			}
		} else {
			if ( set.hasPCDATA() ) {
				res.append( "VALUE" );
			} else
				if ( set.getType() == ElementSetDTDNode.CHOICE_TYPE ) {
					for ( int i = 0; i < set.getChildCount(); i++ ) {
						DTDNode node = set.getDTDNodeAt( i );
						if ( node.isElementSet() ) {
							ElementSetDTDNode set2 = ( ElementSetDTDNode )node;
							if ( set2.getOperator() == ElementSetDTDNode.ZERO_MORE_ITEM_OPERATOR ||
									set2.getOperator() == ElementSetDTDNode.ZERO_ONE_ITEM_OPERATOR ) {								
							} else {
								// Good one
								buildElementSet( res, set2, root );
								break;
							}
						} else {
							if ( node.isElementRef() ) {
								ElementRefDTDNode ref2 = ( ElementRefDTDNode )node;
								if ( ref2.getOperator() == ElementSetDTDNode.ZERO_MORE_ITEM_OPERATOR ||
										ref2.getOperator() == ElementSetDTDNode.ZERO_ONE_ITEM_OPERATOR ) {								
								} else {
									// Good one
									buildNode( res, node, root );
									break;
								}								
							}
						}
					}
				}
		}
	}

	private void buildAttribute( StringBuffer res, AttributeDTDNode att ) {
		if ( att.getUsage() == AttributeDTDNode.REQUIRED_ATT )
			res.append( " " ).append( att.getName() ).append( "=\"VALUE\"" );
	}

}
