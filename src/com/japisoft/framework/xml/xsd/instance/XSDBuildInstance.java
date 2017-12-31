package com.japisoft.framework.xml.xsd.instance;

import com.japisoft.framework.xml.parser.node.FPNode;

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
public class XSDBuildInstance {

	public void buildElement( StringBuffer res, String elementName, FPNode schemaRoot, String schemaURI, boolean rootMode ) {
		FPNode element = searchGlobalDefinition( "element", elementName, schemaRoot );
		if ( element != null ) {
			buildElement( res, element, schemaRoot, schemaURI, rootMode );
		}
	}

	private void buildElement( StringBuffer res, FPNode element, FPNode schemaRoot, String schemaURI, boolean rootMode ) {
		
		int minOccurs = 1;
		
		if ( element.hasAttribute( "ref" ) ) {
			if ( element.hasAttribute( "minOccurs" ) ) {
				minOccurs = Integer.parseInt( element.getAttribute( "minOccurs" ) );
			}
			element = searchGlobalDefinition( "element", element.getAttribute( "ref" ), schemaRoot);
			if ( element == null )
				return;
		} else {
			minOccurs = Integer.parseInt( element.getAttribute( "minOccurs", "1" ) );
		}

		if ( minOccurs == 0 && !rootMode )
			return;
		
		if ( minOccurs > 1 && !rootMode ) {
			for ( int j = 0; j < minOccurs; j++ )
				buildWithoutOccurence( res, element, schemaRoot, schemaURI, rootMode );
		} else
			buildWithoutOccurence( res, element, schemaRoot, schemaURI, rootMode );

	}

	private void buildWithoutOccurence( StringBuffer res, FPNode element, FPNode schemaRoot, String schemaURI, boolean rootMode ) {
		String target = resolveNamespace( element );
		res.append( "<" ).append( element.getAttribute( "name", "error" ) );
		if ( target != null ) {
			res.append( " xmlns=\"" ).append( target ).append( "\"" );
		}
		
		if ( rootMode ) {
			res.append( " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" );
			
			if ( target != null ) {
				res.append( " xsi:schemaLocation=\"" ). 
					append( target ).append( " " ). 
						append( schemaURI ).
							append( "\"" );
			} else 
				res.append( " xsi:noNamespaceSchemaLocation=\"" ).
					append( schemaURI ).
						append( "\"" );

		}

		StringBuffer content = new StringBuffer();
		resolveElementType( content, element, schemaRoot );

		if ( content.length() == 0 ) {
		}
		else {	
			res.append( content );
		}
	}

	private void resolveElementType( StringBuffer res, FPNode element, FPNode root ) {
		
		FPNode elementType = getElementType( element, root );
		if ( elementType == null ) {

			if ( element.hasAttribute( "type" ) ) {

				// Doubt a default simple type
				res.append( ">VALUE" );
				res.append( "</" ).append( element.getAttribute( "name" ) ).append( ">" );
				
			} else {
				res.append( "/>" );
				return;	
			}

		}
		else {
			if ( elementType.matchContent( "simpleType" ) ) {
				// Sure no attribute required
				res.append( ">VALUE" );
				res.append( "</" ).append( element.getAttribute( "name" ) ).append( ">" );
			} else {
				if ( elementType.matchContent( "complexType" ) ) {
					
					resolveAttributes( res, elementType, root );
					
					res.append( ">" );
					
					// Build the element content
					FPNode tmpNode = elementType.getFirstChildByName( "complexContent" );
					if ( tmpNode != null ) {
						// Search for extension
						tmpNode = tmpNode.getFirstChildByName( "extension" );
						if ( tmpNode != null ) {
							String base = tmpNode.getAttribute( "base" );
							// Search this complexType
							
							elementType = searchGlobalDefinition( "complexType", base, root );
					
							if ( elementType == null )	// ?
								return;
						}
					} else
						if ( elementType.getFirstChildByName( "simpleContent" ) != null ) {
							res.append( "VALUE" );
						}

					// Resolve the elementType

					for ( int i = 0; i < elementType.childCount(); i++ ) {
						
						FPNode c = elementType.childAt( i );
						
						if ( c.matchContent( "sequence" ) )
							resolveSequence( res, c, root );
						else
						if ( c.matchContent( "choice" ) ) {
							resolveChoice( res, c, root );							
						} else
						if ( c.matchContent( "all" ) ) {
							resolveAll( res, c, root );
						} else
						if ( c.matchContent( "group" ) ) {
							resolveGroup( res, c, root );
						}

					}


					if ( tmpNode != null ) {	// Add the extension element
						for ( int i = 0; i < tmpNode.childCount(); i++ ) {
							FPNode c = tmpNode.childAt( i );

							if ( c.matchContent( "element" ) )
								buildElement( res, c, root, null, false );
							else
							if ( c.matchContent( "sequence" ) )
								resolveSequence( res, c, root );
							else
							if ( c.matchContent( "choice" ) ) {
								resolveChoice( res, c, root );							
							} else
							if ( c.matchContent( "all" ) ) {
								resolveAll( res, c, root );
							} else
							if ( c.matchContent( "group" ) ) {
								resolveGroup( res, c, root );
							}
						
						}
					}

					// Close it
					
					res.append( "</" ).append( element.getAttribute( "name" ) ).append( ">" );
				}
			}
		}
	}

	private void resolveSequence( StringBuffer res, FPNode seq, FPNode root ) {
		try {
			int minOccurs = Integer.parseInt( seq.getAttribute( "minOccurs", "1" ) );
			if ( minOccurs == 0 )
				return;
			
			for ( int i = 0; i < seq.childCount(); i++ ) {

				if ( "0".equals( seq.childAt( i ).getAttribute( "minOccurs", "1" ) ) )
					continue;
								
				if ( seq.childAt( i ).matchContent( "element" ) )
					buildElement( res, seq.childAt( i ), root, null, false );
				else
				if ( seq.childAt( i ).matchContent( "group" ) )
					resolveGroup( res, seq.childAt( i ), root );
				
			}

		} catch (NumberFormatException e) {
		}
	}

	private void resolveChoice( StringBuffer res, FPNode ch, FPNode root ) {
		try {
			int minOccurs = Integer.parseInt( ch.getAttribute( "minOccurs", "1" ) );
			if ( minOccurs == 0 )
				return;
			
			for ( int i = 0; i < ch.childCount(); i++ ) {

				if ( "0".equals( ch.childAt( i ).getAttribute( "minOccurs", "1" ) ) )
						continue;
				
				if ( ch.childAt( i ).matchContent( "element" ) )
					buildElement( res, ch.childAt( i ), root, null, false );
				else
				if ( ch.childAt( i ).matchContent( "group" ) )
					resolveGroup( res, ch.childAt( i ), root );

				break;
			}
			
		} catch (NumberFormatException e) {
		}		
	}

	private void resolveAll( StringBuffer res, FPNode all, FPNode root ) {
		resolveSequence( res, all, root );
	}

	private void resolveGroup( 
			StringBuffer res, 
			FPNode gp,
			FPNode root ) {
		if ( gp.hasAttribute( "ref" ) ) {
			gp = searchGlobalDefinition( 
					"group", 
					gp.getAttribute( "ref" ), 
					root );
			if ( gp == null )
				return;
		}

		for ( int i = 0; i < gp.childCount(); i++ ) {
			if ( gp.childAt( i ).matchContent( "sequence" ) ) {
				resolveSequence( res, gp.childAt( i ), root );
			} else 
				if ( gp.childAt( i ).matchContent( "choice" ) ) {
					resolveChoice( res, gp.childAt( i ), root );
				} else
					if ( gp.childAt( i ).matchContent( "all" ) ) {
						resolveAll( res, gp.childAt( i ), root );
					}
		}
	}

	private void resolveAttributes( StringBuffer res, FPNode type, FPNode root ) {
		
		// Resolve attribute
		for ( int i = 0; i < type.childCount(); i++ ) {
			if ( type.childAt( i ).matchContent( "attribute" ) ) {
				buildAttribute( res, type.childAt( i ), root );
			} else
			if ( type.childAt( i ).matchContent( "attributeGroup" ) ) {
				buildAttributeGroup( res, type.childAt( i ), root );							
			}
		}

		// We may have inherited complexContent or simpleContent
		for ( int i = 0; i < type.childCount(); i++ ) {
			FPNode c = type.childAt( i );
			
			if ( c.matchContent( "complexContent" )
					|| c.matchContent( "simpleContent" ) ) {
				FPNode ref = c.getFirstChildByName( "restriction" );
				if ( ref == null )
					ref = c.getFirstChildByName( "extension" );
				if ( ref != null ) {
					FPNode refType = searchGlobalDefinition( "complexType", ref.getAttribute( "base" ), root );
					if ( refType != null )
						resolveAttributes( res, refType, root );
					else {
						// Check for attributes inside
						resolveAttributes( res, ref, root );
					}
				}
			}
		}

	}

	private void buildAttribute( StringBuffer res, FPNode attribute, FPNode root ) {
		if ( attribute.hasAttribute( "ref" ) ) {
			attribute = searchGlobalDefinition( "attribute", attribute.getAttribute( "ref" ), root );
			if ( attribute == null )
				return;
		}
		// Must add !
		if ( "required".equals( attribute.getAttribute( "use" ) ) ) {
			res.append( " " );
			res.append( attribute.getAttribute( "name", "error" ) );
			res.append( "=\"VALUE\"" );
		}
	}

	private void buildAttributeGroup( StringBuffer res, FPNode attributeGrp, FPNode root ) {
		if ( attributeGrp.hasAttribute( "ref" ) ) {
			attributeGrp = searchGlobalDefinition( "attributeGroup", attributeGrp.getAttribute( "ref" ), root );
			if ( attributeGrp == null )
				return;
		}		
		for ( int i = 0; i < attributeGrp.childCount(); i++ ) {
			if ( attributeGrp.childAt( i ).matchContent( "attribute" ) ) 
				buildAttribute( res, attributeGrp.childAt( i ), root );
		}
	}
		
	private FPNode getElementType( FPNode element, FPNode root ) {
		
		FPNode res = element.getFirstChildByName( "simpleType" );
		if ( res != null )
			return res;
		else {
			res = element.getFirstChildByName( "complexType" );
			if ( res != null )
				return res;
			else {
				String type = element.getAttribute( "type" );
				if ( type != null ) {
					res = searchGlobalDefinition( "simpleType", type, root );
					if ( res != null )
						return res;
					else {
						res = searchGlobalDefinition( "complexType", type, root );
						if ( res != null )
							return res;
					}
				}
			}
		}
		
		return null;
	}
	

	private String resolveNamespace( FPNode node ) {

		if ( node == null )
			return null;
		
		if ( node.getApplicationObject() != null && !"override".equals( node.getApplicationObject() ) )
			return ( String )node.getApplicationObject();
		else 
			if ( node.hasAttribute( "targetNamespace" ) )
				return node.getAttribute( "targetNamespace" );
			else
				// Try with the parent
				return resolveNamespace( node.getFPParent() );

	}

	private FPNode searchGlobalDefinition( String type, String name, FPNode schemaRoot ) {
		if ( name != null ) {
			
			String namespace = null;
			
			if  ( name.contains( "{" ) ) {
				int j = name.lastIndexOf( "}" );
				namespace = name.substring( 1, j );
				name = name.substring( j + 1 );
			}

			for ( int i = 0; i < schemaRoot.childCount(); i++ ) {
				FPNode sn = schemaRoot.childAt( i );
				if ( sn.matchContent( type ) ) {
					
					String typeName = sn.getAttribute( "name" );
					if ( name.equals( typeName ) ) {

						if ( namespace == null )
							return sn;
						
						if ( namespace != null && 
								namespace.equals( sn.getApplicationObject() ) )
							return sn;
					
					}
				}
			}
		}
		return null;
	}
	
}
