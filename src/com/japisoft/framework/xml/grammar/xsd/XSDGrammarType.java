package com.japisoft.framework.xml.grammar.xsd;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.framework.xml.DOMToolkit;
import com.japisoft.framework.xml.grammar.GrammarElement;
import com.japisoft.framework.xml.grammar.GrammarNode;
import com.japisoft.framework.xml.grammar.GrammarText;
import com.japisoft.framework.xml.grammar.GrammarType;

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
public class XSDGrammarType implements GrammarType {

	private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

	private String nameType = null;

	public XSDGrammarType( String type ) {
		this.nameType = type;
	}

	private Document doc = null;
	private XSDGrammar grammar = null;
	
	public XSDGrammarType( XSDGrammar grammar, Element definitionType ) {
		this.grammar = grammar;
		doc = definitionType.getOwnerDocument();
		try {
			processElement( definitionType );
		} finally {
			doc = null;
		}
	}

	public String infer() {
		if ( "boolean".equals( nameType ) ) {
			return "false";
		} else
		if ( "float".equals( nameType ) ) {
			return "1.0";
		} else
		if ( "double".equals( nameType ) ) {
			return "1.0";
		} else
		if ( "integer".equals( nameType ) ) {
			return "1";
		} else
		if ( "nonPositiveInteger".equals( nameType ) )
			return "-1";
		else
		if ( "negativeInteger".equals( nameType ) )
			return "-1";
		else
		if ( "long".equals( nameType ) ) {
			return "1";
		} else
		if ( "byte".equals( nameType ) ) {
			return "1";
		} else
		if ( "nonNegativeInteger".equals( nameType ) ) {
			return "1";
		} else
		if ( "unsignedLong".equals( nameType ) ) {
			return "1";
		} else
		if ( "unsignedInt".equals( nameType ) ) {
			return "1";
		} else
		if ( "unsignedShort".equals( nameType ) ) {
			return "1";
		} else
		if ( "unsignedByte".equals( nameType ) ) {
			return "1";
		} else
		if ( "positiveInteger".equals( nameType ) ) {
			return "1";
		}
		return "";
	}

	public XSDGrammar getGrammar() {
		return grammar;
	}

	private void processElement( Element definitionType ) {

		Element refType = 
			definitionType;

		if ( definitionType.hasAttribute( "ref" ) ) {
			definitionType = resolveRef( definitionType );
			if ( definitionType == null ) {
				// Can't find the reference
				definitionType = refType;
			}
		}

		nameType = 
			definitionType.getAttribute( "type" );

		if ( definitionType.hasAttribute( "base" ) )
			nameType = definitionType.getAttribute( "base" );

		Element explorableType = null;

		// Look at children in first
		NodeList nl = definitionType.getChildNodes();
		for  ( int i = 0; i < nl.getLength(); i++ ) {
			if ( !( nl.item( i ) instanceof Element ) )
				continue;

			Element e = ( Element )nl.item( i );
			if ( "simpleType".equals( e.getLocalName() ) ) { 
					explorableType = e;
					break;
			} else 
				if ( "complexType".equals( e.getLocalName() ) ) {
					explorableType = e;
					break;
				}
		}

		if ( explorableType == null && 
				!"".equals( nameType ) ) {
			// Search this type
			explorableType = 
				resolveType( nameType );
		}

		if ( explorableType != null ) {

			if ( "simpleType".equals( 
					explorableType.getLocalName() ) )
				processSimpleType( 
						explorableType );
			else
				processComplexType( 
						refType, 
						explorableType );

		} else {

			// If we can't resolve it then this is a default one
			nameType = extractLocalType(
					definitionType.getAttribute( "type" ), 
					definitionType );

		}

	}

	private void processComplexContent( Element complexContent ) {

		Element e = 
			DOMToolkit.getFirstElement( 
				complexContent, 
				"restriction" );

		if ( e == null ) {
			e = DOMToolkit.getFirstElement( 
				complexContent, 
				"extension" );
		}			

		if ( e != null ) {
			
			String type = e.getAttribute( "base" );

			Element resolveBase = resolveType( type );
			if ( resolveBase != null ) {

				processElement( resolveBase );

			}

			processComplexParts( complexContent );
		}
		
	}

	private String processSimpleContent( Element simpleContent ) {
		Element e = 
			DOMToolkit.getFirstElement( 
				simpleContent, 
				"restriction" );

		if ( e == null ) {
			e = DOMToolkit.getFirstElement( 
				simpleContent, 
				"extension" );
		}			

		if ( e != null ) {
			String type = e.getAttribute( "base" );
			Element resolveBase = resolveType( type );

			if ( resolveBase != null ) {

				processSimpleType( resolveBase );

			} else {

				String resType = 
					extractLocalType( type, e );

				if ( resType != null ) {

					GrammarNode node = 
						getFirstTextGrammarNode();

					if ( node != null ) {

						removeGrammarNode( node );
						
					}

					addGrammarNodeValue( 
							new XSDGrammarText(
									null, 
									new XSDGrammarType( 
											resType )  ) );

				}

			}

			processComplexParts( e );

			return type;
		}

		return null;
	}

	/** For complexType, simpleContent, complexContent */
	void processComplexParts( Element complexPart ) {

		NodeList nlext = complexPart.getChildNodes();

		if ( nlext.getLength() > 0 ) {
			for ( int i = 0; i < nlext.getLength(); i++ ) {
				Node n = nlext.item( i );
				if ( n instanceof Element ) {
					if ( SCHEMA_NS.equals( 
							n.getNamespaceURI() ) ) {
						if ( "sequence".equals( n.getLocalName() ) ||
								"choice".equals( n.getLocalName() ) ||
									"all".equals( n.getLocalName() ) ) {	
							addGrammarNodeValue(
									new XSDGrammarContainer( grammar, ( Element )n ) );
						} else
						if ( "element".equals( n.getLocalName() ) ) {
							addElementValue( ( Element )n );
						} else
						if ( "attribute".equals( n.getLocalName() ) ) {
							addAttributeValue( ( Element )n );
						}
					}
				}
			}
		}
	}

	void processSimpleType(
			Element simpleType ) {

		Element e = 
			DOMToolkit.getFirstElement( 
				simpleType, 
				"restriction" );

		if ( e == null ) {
			e = DOMToolkit.getFirstElement( 
				simpleType, 
				"extension" );
		}			

		if ( e != null ) {

			// Explore the parent type
			
			nameType = e.getAttribute( "base" );
			Element resolveBase = resolveType( nameType );
			if ( resolveBase != null ) {

				processSimpleContent( resolveBase );

			}

		} else {
			 
			e = DOMToolkit.getFirstElement( simpleType, "list" );
			if ( e != null ) {
				nameType = "list";
			} else {
				e = DOMToolkit.getFirstElement( simpleType, "union" );
				if ( e != null ) {
					nameType = "union";
				}				
			}

			
		}

		// Get all the enumeration

		NodeList nlEnum = simpleType.getElementsByTagNameNS( SCHEMA_NS, "enumeration" );

		for ( int i = 0; i < nlEnum.getLength(); i++ ) {
			addEnumValue( 
					( Element )nlEnum.item( i ) 
			);
		}

	}

	public boolean isComplex() {
		return "complex".equals( nameType );
	}

	void processComplexType( 
			Element refType, 
			Element complexType ) {
		
		nameType = "complex";		

		Element sc = 
			DOMToolkit.getFirstElement(
					complexType, 
					"simpleContent" );

		if ( sc != null ) {

			processSimpleContent( sc );

		} else {

			sc = 
				DOMToolkit.getFirstElement(
						complexType, 
						"complexContent" );

			if ( sc != null ) {

				processComplexContent( sc );

			} else {

				processComplexParts( complexType );

			}

		}

	}

/*	private Element resolveType( String type ) {
		List<GrammarElement> l = 
			XSDGrammar.getGlobalComponent( 
					doc, "simpleType" );
		for ( GrammarElement ge : l ) {
			Element e = ( ( XSDGrammarElement )ge ).getDOMElement();
			if ( e.getAttribute( "name" ).equals( type ) )
				return e;
		}
		l = 
			XSDGrammar.getGlobalComponent( 
					doc, "complexType" );
		for ( GrammarElement ge : l ) {
			Element e = ( ( XSDGrammarElement )ge ).getDOMElement();
			if ( e.getAttribute( "name" ).equals( type ) )
				return e;
		}
		return null;
	} */

	private Element resolveType( String definitionType ) {
		NodeList nl = 
			doc.getDocumentElement().getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				if ( "simpleType".equals( e.getLocalName() ) ||
						"complexType".equals( e.getLocalName() ) ) {
					if ( definitionType.equals( e.getAttribute( "name" ) ) ) {
						return e;
					}
				}
			}
		}
		return null;
	}

	private Element resolveRef( Element definitionType ) {
		String ref = definitionType.getAttribute( "ref" );
		List<GrammarElement> l =  XSDGrammar.getGlobalComponent(
				grammar,
				doc, 
				"element" 
		);
		for ( GrammarElement ge : l ) {
			Element e = ( ( XSDGrammarElement )ge ).getDOMElement();
			if ( e.getAttribute( "name" ).equals( ref ) )
				return e;
		}
		// Not found ??
		return null;
	}

	public String getType() {
		return nameType;
	}

	private void addEnumValue( Element e ) {
		addGrammarNodeValue( 
				new XSDGrammarText(
						grammar,
						e.getAttribute( "value" ) ) );
	}

	private void addAttributeValue( Element e ) {
		addGrammarNodeValue(
			new XSDGrammarAttribute( grammar, e ) 
		);		
	}

	private void addElementValue( Element e ) {
		addGrammarNodeValue( 
			new XSDGrammarElement( grammar, e ) );		
	}

	private void addGrammarNodeValue( GrammarNode e ) {
		if ( children == null )
			children = new ArrayList<GrammarNode>();
		children.add( e );
	}
	
	private void removeGrammarNode( GrammarNode e ) {
		if ( children != null ) {
			children.remove( e );
		}
	}

	private GrammarText getFirstTextGrammarNode() {
		if ( children == null )
			return null;
		for ( GrammarNode node : children ) {
			if ( node instanceof GrammarText )
				return ( GrammarText )node;
		}
		return null;
	}

	private ArrayList<GrammarNode> children = null;

	public List<GrammarNode> getValues() {
		return children;
	}

	private String extractLocalType( String type, Element e ) {
		if ( type != null ) {
			String prefix = getRootPrefix( e );
			if ( prefix != null ) {
				if ( type.startsWith( 
						prefix + ":" ) ) {
					type = type.substring( 
							prefix.length() + 1 );
					return type;
				}
			}
		}
		return null;
	}

	protected String getRootPrefix( Node node ) {
		Document doc = node.getOwnerDocument();
		Element root = doc.getDocumentElement();
		return root.getPrefix();
	}	

}
