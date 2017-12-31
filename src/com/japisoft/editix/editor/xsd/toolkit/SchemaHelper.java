package com.japisoft.editix.editor.xsd.toolkit;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.japisoft.xmlpad.toolkit.XMLFileData;
import com.japisoft.xmlpad.toolkit.XMLToolkit;

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
public final class SchemaHelper {

	public static String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	
	public static String[] FACETS = new String[] { "enumeration",
			"fractionDigits", "length", "minExclusive", "minInclusive",
			"minLength", "maxExclusive", "maxInclusive", "maxLength",
			"pattern", "totalDigits", "whiteSpace", };

	public static String[] PTYPES = new String[] { "string", "boolean",
			"float", "double", "decimal", "duration", "dateTime", "time",
			"date", "gYearMonth", "gYear", "gMonthDay", "gDay", "gMonth",
			"hexBinary", "base64Binary", "anyURI", "QName", "NOTATION",
			"normalizedString", "token", "language", "IDREFS", "ENTITIES",
			"NMTOKEN", "NMTOKENS", "Name", "NCName", "ID", "IDREF", "ENTITY",
			"integer", "nonPositiveInteger", "negativeInteger", "long", "int",
			"short", "byte", "nonNegativeInteger", "unsignedLong",
			"unsignedInt", "unsignedShort", "unsignedByte", "positiveInteger" };

	/*
	 * <element abstract = boolean : false block = (#all | List of (extension |
	 * restriction | substitution)) default = string final = (#all | List of
	 * (extension | restriction)) fixed = string form = (qualified |
	 * unqualified) id = ID maxOccurs = (nonNegativeInteger | unbounded) : 1
	 * minOccurs = nonNegativeInteger : 1 name = NCName nillable = boolean :
	 * false ref = QName substitutionGroup = QName type = QName
	 */

	/*
	 * <complexType abstract = boolean : false block = (#all | List of
	 * (extension | restriction)) final = (#all | List of (extension |
	 * restriction)) id = ID mixed = boolean : false name = NCName
	 */

	/*
	 * <simpleType final = (#all | List of (list | union | restriction)) id = ID
	 * name = NCName {any attributes with non-schema namespace . . .}> Content:
	 * (annotation?, (restriction | list | union)) </simpleType>
	 */

	/*
	 * <attribute default = string fixed = string form = (qualified |
	 * unqualified) id = ID name = NCName ref = QName type = QName use =
	 * (optional | prohibited | required) : optional
	 */

	/*
	 * <attributeGroup id = ID name = NCName ref = QName
	 */

	/*
	 * <complexContent id = ID mixed = boolean
	 */

	/*
	 * <restriction base = QName id = ID
	 */

	/*
	 * <anyAttribute id = ID namespace = ((##any | ##other) | List of (anyURI |
	 * (##targetNamespace | ##local)) ) : ##any processContents = (lax | skip |
	 * strict) : strict
	 */

	/*
	 * <group id = ID maxOccurs = (nonNegativeInteger | unbounded) : 1 minOccurs =
	 * nonNegativeInteger : 1 name = NCName ref = QName
	 */

	/*
	 * <all id = ID maxOccurs = 1 : 1 minOccurs = (0 | 1) : 1 {any attributes
	 * with non-schema namespace . . .}> Content: (annotation?, element*) </all>
	 * 
	 * <choice id = ID maxOccurs = (nonNegativeInteger | unbounded) : 1
	 * minOccurs = nonNegativeInteger : 1 {any attributes with non-schema
	 * namespace . . .}> Content: (annotation?, (element | group | choice |
	 * sequence | any)*) </choice>
	 * 
	 * <sequence id = ID maxOccurs = (nonNegativeInteger | unbounded) : 1
	 * minOccurs = nonNegativeInteger : 1 {any attributes with non-schema
	 * namespace . . .}> Content: (annotation?, (element | group | choice |
	 * sequence | any)*) </sequence>
	 */

	/*
	 * <any id = ID maxOccurs = (nonNegativeInteger | unbounded) : 1 minOccurs =
	 * nonNegativeInteger : 1 namespace = ((##any | ##other) | List of (anyURI |
	 * (##targetNamespace | ##local)) ) : ##any processContents = (lax | skip |
	 * strict) : strict
	 */

	/*
	 * <include id = ID schemaLocation = anyURI
	 */

	/*
	 * <redefine id = ID schemaLocation = anyURI
	 */

	/*
	 * <import id = ID namespace = anyURI schemaLocation = anyURI
	 */

	/*
	 * <annotation id = ID {any attributes with non-schema namespace . . .}>
	 * Content: (appinfo | documentation)* </annotation>
	 * 
	 * <appinfo source = anyURI {any attributes with non-schema namespace . .
	 * .}> Content: ({any})* </appinfo>
	 * 
	 * <documentation source = anyURI xml:lang = language
	 */

	/*
	 * <notation id = ID name = NCName public = token system = anyURI
	 */
	
	public static XSDAttribute[] getAttributesForElement(String name, boolean designerMode ) {
		if ("notation".equals(name)) {
			return new XSDAttribute[] { new XSDAttribute("id", null, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("public", null, true),
					new XSDAttribute("system", null, true) };
		} else if ("element".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("abstract", XSDAttribute.BOOLEAN_TYPE,
							null, false),
					new XSDAttribute("block", new String[] { "#all",
							"extension", "restriction", "substitution" }, true),
					new XSDAttribute("default", null, true),
					new XSDAttribute("final", new String[] { "#all" }, true),
					new XSDAttribute("fixed", null, true),
					new XSDAttribute("form", new String[] { "qualified",
							"unqualified" }, false),
					new XSDAttribute("id", null, true),
					new XSDAttribute("maxOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "unbounded" }, true),
					new XSDAttribute("minOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "1" }, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("nillable", XSDAttribute.BOOLEAN_TYPE,
							null, false),
					new XSDAttribute("ref", XSDAttribute.ELEMENT_REF, null,
							true),
					new XSDAttribute("substitutionGroup",
							XSDAttribute.ELEMENT_REF, null, true),
					new XSDAttribute("type (base)", XSDAttribute.TYPE_REF,
							null, true) };
		} else if ("complexType".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("abstract", XSDAttribute.BOOLEAN_TYPE,
							null, false),
					new XSDAttribute("block", new String[] { "#all",
							"extension", "restriction" }, true),
					new XSDAttribute("final", new String[] { "#all",
							"extension", "restriction" }, true),
					new XSDAttribute("id", null, true),
					new XSDAttribute("mixed", XSDAttribute.BOOLEAN_TYPE, null,
							false), new XSDAttribute("name", null, true) };
		} else if ("simpleType".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("final", new String[] { "#all" }, true),
					new XSDAttribute("id", null, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("type (base)", XSDAttribute.TYPEP_REF,
							null, true) };
		} else if ("attribute".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("default", null, true),
					new XSDAttribute("fixed", null, true),
					new XSDAttribute("form", new String[] { "qualified",
							"unqualified" }, false),
					new XSDAttribute("id", null, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("ref", XSDAttribute.ATTRIBUTE_REF, null,
							true),
					new XSDAttribute("type (base)", XSDAttribute.TYPEP_REF,
							null, true),
					new XSDAttribute("use", new String[] { "optional",
							"prohibited", "required" }, false) };
		} else if ("attributeGroup".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("ref", XSDAttribute.ATTRIBUTE_GROUP_REF, null,
							true) };
		} else if ("complexContent".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("mixed", XSDAttribute.BOOLEAN_TYPE, null,
							false) };
		} else if ("restriction".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("base", XSDAttribute.TYPE_REF, null, true),
					new XSDAttribute("id", null, true) };
		} else if ("extension".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("base", XSDAttribute.TYPE_REF, null, true),
					new XSDAttribute("id", null, true) };
		} else if ("anyAttribute".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("namespace", new String[] { "##any",
							"##other" }, true) };
		} else if ("group".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("maxOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "unbounded" }, true),
					new XSDAttribute("minOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "1" }, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("ref", XSDAttribute.GROUP_REF, null, true) };
		} else if ("all".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("maxOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "1" }, false),
					new XSDAttribute("minOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER, new String[] {
									"0", "1" }, false) };
		} else if ("choice".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("maxOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "unbounded" }, true),
					new XSDAttribute("minOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "1" }, true) };
		} else if ("sequence".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("maxOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "unbounded" }, true),
					new XSDAttribute("minOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "1" }, true) };
		} else if ("any".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("maxOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "unbounded" }, true),
					new XSDAttribute("minOccurs",
							XSDAttribute.NON_NEGATIVE_INTEGER,
							new String[] { "1" }, true),
					new XSDAttribute("namespace", new String[] { "##any",
							"##other" }, true),
					new XSDAttribute("processContents", new String[] { "lax",
							"skip", "strict" }, false) };
		} else if ("include".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("schemaLocation", XSDAttribute.SCHEMA_REF,
							null, true) };
		} else if ("redefine".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("schemaLocation", XSDAttribute.SCHEMA_REF,
							null, true) };
		}
		if ("import".equals(name)) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("namespace", null, true),
					new XSDAttribute("schemaLocation", XSDAttribute.SCHEMA_REF,
							null, true) };
		} else if ("annotation".equals(name)) {
			return new XSDAttribute[] { new XSDAttribute("id", null, true) };
		} else if ("appinfo".equals(name)) {
			return new XSDAttribute[] { new XSDAttribute("source", null, true) };
		} else if ("documentation".equals(name)) {
			return new XSDAttribute[] { new XSDAttribute("source", null, true),
					new XSDAttribute("xml:lang", null, true) };
		} else if ( "key".equals( name )) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("ref", null, true),
					new XSDAttribute("xpathDefaultNamespace", new String[] { "##defaultNamespace",
					"##targetNamespace", "##local" }, true)
			};
		} else if ( "keyref".equals( name ) ) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("ref", null, true),
					new XSDAttribute("refer", null, true),
					new XSDAttribute("xpathDefaultNamespace", new String[] { "##defaultNamespace",
					"##targetNamespace", "##local" }, true)
			};
		} else if ( "unique".equals( name ) ) {
			return new XSDAttribute[] {
					new XSDAttribute("id", null, true),
					new XSDAttribute("name", null, true),
					new XSDAttribute("ref", null, true),
					new XSDAttribute("xpathDefaultNamespace", new String[] { "##defaultNamespace",
					"##targetNamespace", "##local" }, true)
			};			
		}
		return null;
	}

	// Anti loop

	public static void unmark( Element e ) {
		NodeList nl = e.getOwnerDocument().getElementsByTagNameNS( SCHEMA_NS, "*" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			nl.item( i ).setUserData( "mark", null, null );
		}
	}

	public static void dumpMark( Element e ) {
		/*
		Document doc = e.getOwnerDocument();
		NodeList nl = doc.getElementsByTagNameNS( SCHEMA_NS, "*" );
		System.out.println( "** Dump Mark" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				System.out.println( " - " + nl.item( i ).getNodeName() + " : " + isMarked( ( Element )nl.item( i ) ) );
			}
		}
		*/
	}

	public static void mark( Element e ) {
		e.setUserData( "mark", Boolean.TRUE, null );
	}
	
	public static boolean isMarked( Element e ) {
		return e.getUserData( "mark" ) == Boolean.TRUE;
	}
	
	public static boolean isAncestor( Node ancestor, Node node ) {
		if ( node == ancestor )
			return true;
		while ( node != ancestor ) {
			node = node.getParentNode();
			if ( node == null )
				return false;
		}
		return true;
	}
	
	private static String[] getChildrenForComplexType( Element e ) {
		String[] uniques = {
			"sequence",
			"choice",
			"all",
			"group"
		};

		boolean foundUnique = false;
		
		for ( String u : uniques ) {
			if ( hasChild( e, u ) ) {
				foundUnique = true;
				break;
			}
		}
		
		if ( foundUnique ) {
			return new String[] {
				"attribute",
				"attributeGroup",
				"anyAttribute"
			};
		} else {
			return new String[] {
					"sequence",
					"choice",
					"all",
					"group",
					"attribute",
					"attributeGroup",
					"anyAttribute"
				};			
		}

	}

	public static boolean isImportation( Element e ) {
		String name = e.getLocalName();
		return "include".equals( name ) || 
					"import".equals( name ) || 
						"redefine".equals( name );
	}

	public static boolean isComplexTypeChild( String nodeName ) {
		return nodeName.equals(	"sequence" ) ||
				nodeName.equals( "choice" ) ||
					nodeName.equals( "all" ) ||
						nodeName.equals( "group" ) ||
							nodeName.equals( "attribute" ) ||
								nodeName.equals( "attributeGroup" ) ||
									nodeName.equals( "anyAttribute" );
	}

	public static String[] getChildrenForElement(Element e) {
		String name = e.getLocalName();
		
		ArrayList<String> res = new ArrayList<String>();
		
		if ( "element".equals( name ) ) {
			if (!hasChild(e, "simpleType")) {
				Element complexType = getFirstChild( e, "complexType" );
				if ( complexType != null ) {
					String[] tmp = getChildrenForElement( complexType );
					for ( String t : tmp )
						res.add( t );
				} else {
					res.add( "sequence" );
					res.add( "choice" );
					res.add( "all" );
					res.add( "group" );
					res.add( "attribute" );
					res.add( "attributeGroup" );
					res.add( "anyAttribute" );
				}	
				res.add( "unique" );
				res.add( "key" );
				res.add( "keyref" );
			}
		} else 
		if ( "complexType".equals( name ) ) {
			String[] tmp = getChildrenForComplexType( e );
			for ( String t : tmp ) {
				res.add( t );
			}
		} else
		if ("all".equals(name)) {
			// (annotation?, element*)
			return new String[] { "element" };
		} else if ("choice".equals(name)) {
			// (annotation?, (element | group | choice | sequence | any)*)
			return new String[] { "element", "group", "choice", "sequence", "any" };
		} else if ("sequence".equals(name)) {
			// (annotation?, (element | group | choice | sequence | any)*)
			return new String[] { "element", "group", "choice", "sequence",
					"any" };
		} else if ("any".equals(name)) {
			// (annotation?)
			return new String[] {};		
		} else
			if ("attributeGroup".equals(name)) {
				// (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
				return new String[] { "attribute", "attributeGroup", "anyAttribute" };
		}

		return res.toArray( new String[ res.size() ] );
		
/*		
		if ("notation".equals(name)) {
			return new String[] {};
		} else if ("element".equals(name)) {
			// (annotation?, ((simpleType | complexType)?, (unique | key |
			// keyref)*))
			if (hasChild(e, "complexType")) {
				return new String[] { "unique", "key", "keyref" };
			} else {
				if (!hasChild(e, "simpleType")) {
					return new String[] { "complexType", "unique", "key",
							"keyref" };
				}
			}
		} else if ("complexType".equals(name)) {
			// (annotation?, (simpleContent | complexContent | ((group | all |
			// choice | sequence)?, ((attribute | attributeGroup)*,
			// anyAttribute?))))

			if (hasChild(e, "simpleContent")) {
				return new String[] { "attribute", "attributeGroup",
						"anyAttribute" };
			} else if (hasChild(e, "complexContent")) {
				return new String[] { "attribute", "attributeGroup",
						"anyAttribute" };
			} else if (hasChild(e, "group")) {
				return new String[] { "attribute", "attributeGroup",
						"anyAttribute" };
			} else if (hasChild(e, "all")) {
				return new String[] { "attribute", "attributeGroup",
						"anyAttribute" };
			} else if (hasChild(e, "choice")) {
				return new String[] { "attribute", "attributeGroup",
						"anyAttribute" };
			} else if (hasChild(e, "sequence")) {
				return new String[] { "attribute", "attributeGroup",
						"anyAttribute" };
			} else {
				return new String[] { "simpleContent", "complexContent",
						"group", "all", "choice", "sequence", "attribute",
						"attributeGroup", "anyAttribute" };
			}

		} else if ("simpleType".equals(name)) {
			// (annotation?, (restriction | list | union))
			return new String[] {};
		} else if ("attribute".equals(name)) {
			// (annotation?, simpleType?)
			return new String[] {};
		} else if ("attributeGroup".equals(name)) {
			// (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
			return new String[] { "attribute", "attributeGroup", "anyAttribute" };
		} else if ("complexContent".equals(name) || "simpleContent".equals( name ) ) {
			// (annotation?, (restriction | extension))
			if (hasChild(e, "restriction")) {
			} else if (hasChild(e, "extension")) {
			} else
				return new String[] { "restriction", "extension" };
		} else if ("restriction".equals(name)) {
			// (annotation?, (group | all | choice | sequence)?, ((attribute |
			// attributeGroup)*, anyAttribute?))
			
			if ( hasChild( e, "group" ) || 
					hasChild( e, "all" ) || 
						hasChild( e, "choice" ) ||
							hasChild( e, "sequence") ) {
				return new String[] { "attribute", "attributeGroup", "anyAttribute" };
			} else {
				return new String[] { "group", "all", "choice", "sequence" };
			}
		} else if ("extension".equals(name)) {
			// (annotation?, ((group | all | choice | sequence)?, ((attribute |
			// attributeGroup)*, anyAttribute?)))
			return new String[] { "group", "all", "choice", "sequence", "attribute"  };
		} else if ("anyAttribute".equals(name)) {
			// (annotation?)
			return new String[] {};
		} else if ("group".equals(name)) {
			// (annotation?, (all | choice | sequence)?)

			if (hasChild(e, "all")) {

			} else if (hasChild(e, "choice")) {

			} else if (hasChild(e, "sequence")) {

			} else
				return new String[] { "all", "choice", "sequence" };
		} else if ("all".equals(name)) {
			// (annotation?, element*)
			return new String[] { "element" };
		} else if ("choice".equals(name)) {
			// (annotation?, (element | group | choice | sequence | any)*)
			return new String[] { "element", "group", "choice", "sequence",
					"any" };
		} else if ("sequence".equals(name)) {
			// (annotation?, (element | group | choice | sequence | any)*)
			return new String[] { "element", "group", "choice", "sequence",
					"any" };
		} else if ("any".equals(name)) {
			// (annotation?)
			return new String[] {};
		} else if ("include".equals(name)) {
			// (annotation?)
			return new String[] {};
		} else if ("redefine".equals(name)) {
			// (annotation | (simpleType | complexType | group |
			// attributeGroup))*
			return new String[] {};
		}
		if ("import".equals(name)) {
			// (annotation?)
			return new String[] {};
		} else if ("annotation".equals(name)) {
			return new String[] {};
		} else if ("appinfo".equals(name)) {
			return new String[] {};
		} else if ("documentation".equals(name)) {
			return new String[] {};
		}
		return null;
		
*/
		
	}

	public static void removeChildren(Element ref) {
		while (ref.getFirstChild() != null) {
			ref.removeChild(ref.getFirstChild());
		}
	}

	public static boolean hasChild(Element ref, String localName) {
		NodeList nl = ref.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				Element e = (Element) nl.item(i);
				if (localName.equals(e.getLocalName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Element getFirstChild( Element parent, String localName ) {
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				Element e = (Element) nl.item(i);
				if (localName.equals(e.getLocalName())) {
					return e;
				}
			}
		}
		return null;
	}

	public static boolean hasChildrenExceptAnnotationAndText( Element ref ) {
		boolean hasChildren = false;
		NodeList nl = ref.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {
				Element e = ( Element )n;
				if ( !"annotation".equals( e.getLocalName() ) ) {
					hasChildren = true;
					break;
				}
			}
		}
		return hasChildren;
	}

	public static String getDocumentation( Element ref ) {
		Element annotation = getChildAt( 
				ref, 
				0, 
				new String[] { "annotation" } );
		if ( annotation != null ) {
			Element documentation = getChildAt( annotation, 0, new String[] { "documentation" } );
			if ( documentation != null ) {
				return getTexts( documentation );
			}
		}
		return null;
	}

	public static boolean createNewAnnotation( Element ref, String content ) {
		// Check for annotation
		NodeList nl = ref.getChildNodes();
		Node firstNode = null;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( firstNode == null )
				firstNode = n;
			if ( n instanceof Element ) {
				Element e = ( Element )n;
				if ( !"annotation".equals( e.getLocalName() ) ) {
					return false;
				}
			}
		}		
		Element annotation = createTag( ref, "annotation" );
		Element documentation = createTag( ref, "documentation" );
		annotation.appendChild( documentation );
		documentation.appendChild(
				ref.getOwnerDocument().createTextNode( content ) );
		if ( firstNode != null )
			ref.insertBefore( annotation, firstNode );
		else
			ref.appendChild( annotation );
		return true;
	}
	
	public static void deleteUnionType(Element ref, int n) {
		NodeList nl = ref.getElementsByTagNameNS(
				"http://www.w3.org/2001/XMLSchema", "union");
		if (nl.getLength() > 0) {
			Element unionElement = (Element) nl.item(0);
			if (unionElement.hasAttribute("memberTypes")) {
				// Update this attribute
				String content = unionElement.getAttribute("memberTypes");
				StringTokenizer st = new StringTokenizer(content, " \t", false);
				StringBuffer al = new StringBuffer();
				while (st.hasMoreTokens()) {
					String t = st.nextToken();
					if (n == 0) {
						// Remove it
						n = -1;
						continue;
					}
					if (al.length() > 0)
						al.append(" ");
					al.append(t);
					n--;
				}
				unionElement.setAttribute("memberTypes", al.toString());
			} else {
				NodeList nl2 = unionElement.getChildNodes();
				for (int i = 0; i < nl2.getLength(); i++) {
					Node nn = nl2.item(i);
					if (nn instanceof Element) {
						Element e = (Element) nn;
						if ("simpleType".equals(e.getLocalName())) {
							if (n == 0) {
								// Delete it
								unionElement.removeChild(e);
								break;
							}
							n--;
						}
					}
				}
			}
		}
	}

	public static void updateUnion(Element ref, int n, String newType) {
		NodeList nl = ref.getElementsByTagNameNS(
				"http://www.w3.org/2001/XMLSchema", "union");
		if (nl.getLength() == 0) {
			Element parent = ref;
			removeChildren(ref);
			// Insert it inside
			if (!"simpleType".equals(ref.getLocalName())) {
				// Create a simpleType and insert inside
				parent = createTag(ref, "simpleType");
				ref.appendChild(parent);
			}
			Element unionElement = createTag(ref, "union");
			unionElement.setAttribute("memberTypes", newType);
			parent.appendChild(unionElement);
			if ("element".equals(ref.getLocalName())
					|| "attribute".equals(ref.getLocalName()))
				ref.removeAttribute("type");
		} else {
			Element unionElement = (Element) nl.item(0);
			if (unionElement.hasAttribute("memberTypes")) {
				// Update this attribute
				String content = unionElement.getAttribute("memberTypes");

				StringTokenizer st = new StringTokenizer(content, " \t", false);
				StringBuffer al = new StringBuffer();
				while (st.hasMoreTokens()) {
					String t = st.nextToken();
					if (n == 0) {
						// Replace it
						t = newType;
					}
					if (al.length() > 0)
						al.append(" ");
					al.append(t);
					n--;
				}
				if (n == 0) {
					if (al.length() > 0)
						al.append(" ");
					al.append(newType);
				}
				unionElement.setAttribute("memberTypes", al.toString());
			} else {
				NodeList nl2 = unionElement.getChildNodes();
				for (int i = 0; i < nl2.getLength(); i++) {
					Node nn = nl2.item(i);
					if (nn instanceof Element) {
						Element e = (Element) nn;
						if ("simpleType".equals(e.getLocalName())) {
							if (n == 0) {
								NodeList nl3 = e.getElementsByTagNameNS(
										"http://www.w3.org/2001/XMLSchema",
										"restriction");
								if (nl3.getLength() > 0) {
									((Element) nl3.item(0)).setAttribute(
											"base", newType);
								}
								n = -1;
								break;
							}
							n--;
						}
					}
				}
				if (n == 0) {
					// Add a new simpleType
					Element simpleType = createTag(ref, "simpleType");
					Element restriction = createTag(ref, "restriction");
					restriction.setAttribute("base", newType);
					simpleType.appendChild(restriction);
					unionElement.appendChild(simpleType);
				}
			}
		}
	}

	public static String[] getUnionTypes(Element ref) {
		NodeList nl = ref.getElementsByTagNameNS(
				"http://www.w3.org/2001/XMLSchema", "union");
		if (nl.getLength() == 0)
			return new String[] {};
		else {
			ArrayList tmp = new ArrayList();
			Element unionElement = (Element) nl.item(0);
			if (unionElement.hasAttribute("memberTypes")) {
				String mt = unionElement.getAttribute("memberTypes");
				StringTokenizer st = new StringTokenizer(mt, " \t", false);
				while (st.hasMoreTokens()) {
					tmp.add(st.nextToken());
				}
			} else {
				// Analysis each simpleType
				NodeList children = unionElement.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node n = children.item(i);
					if (n instanceof Element) {
						if ("simpleType".equals(n.getLocalName())) {
							NodeList nl2 = ((Element) n)
									.getElementsByTagNameNS(
											"http://www.w3.org/2001/XMLSchema",
											"restriction");
							if (nl2.getLength() > 0) {
								Element _ = (Element) nl2.item(0);
								if (_.hasAttribute("base")) {
									tmp.add(_.getAttribute("base"));
								}
							}
						}
					}
				}
			}
			String[] res = new String[tmp.size()];
			for (int i = 0; i < tmp.size(); i++) {
				res[i] = (String) tmp.get(i);
			}
			return res;
		}
	}

	public static Element getAnyByName( Element root, String name ) {
		return getRef( root, "*", name );
	}
	
	public static Element getComplexTypeByName( Element root, String name ) {
		return getRef( root, "complexType", name );
	}
	
	public static String[] getElementRef(Element root) {
		return getRef(root, "element");
	}

	public static String[] getAttributeRef(Element root) {
		return getRef(root, "attribute");
	}

	public static String[] getTypeRef(Element root) {
		return getRef(root, "type");
	}

	public static String[] getGroupRef(Element root) {
		return getRef(root, "group");
	}

	public static String[] getAttributeGroupRef( Element root ) {
		return getRef( root, "attributeGroup" );
	}

	public static Element searchForChildWithAttribute(Element ref,
			String attName) {
		NodeList nl = ref.getElementsByTagName("*");
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				Element e = (Element) nl.item(i);
				if (e.hasAttribute(attName))
					return e;
			}
		}
		return null;
	}

	public static boolean updateListType(Element ref, String newType) {
		NodeList nl = ref.getElementsByTagNameNS(
				"http://www.w3.org/2001/XMLSchema", "list");
		if (nl.getLength() > 0) {
			Element listElement = (Element) nl.item(0);
			if (listElement.hasAttribute("itemType")) {
				listElement.setAttribute("itemType", newType);
				return true;
			} else {
				// Search for simpleType
				NodeList nl2 = listElement.getElementsByTagNameNS(
						"http://www.w3.org/2001/XMLSchema", "restriction");
				if (nl2.getLength() > 0) {
					Element restrictionElement = (Element) nl2.item(0);
					restrictionElement.setAttribute("base", newType);
					return true;
				} else
					return false;
			}
		} else
			return false;
	}

	public static String getListType(Element ref) {
		NodeList nl = ref.getElementsByTagNameNS(
				"http://www.w3.org/2001/XMLSchema", "list");
		if (nl.getLength() > 0) {
			Element listElement = (Element) nl.item(0);
			if (listElement.hasAttribute("itemType")) {
				return listElement.getAttribute("itemType");
			} else {
				// Search for simpleType
				NodeList nl2 = listElement.getElementsByTagNameNS(
						"http://www.w3.org/2001/XMLSchema", "restriction");
				if (nl2.getLength() > 0) {
					Element restrictionElement = (Element) nl2.item(0);
					return restrictionElement.getAttribute("base");
				} else
					return null;
			}
		} else
			return null;
	}

	public static String[] getType(Element root, boolean includeComplexType,
			boolean includeSimpleType) {

		ArrayList res = new ArrayList();

		String[] tmp = null;

		if (includeSimpleType) {
			// Search for simpleType
			tmp = getRef(root, "simpleType");
			for (int i = 0; i < tmp.length; i++) {
				res.add(tmp[i]);
			}
		}

		if (includeComplexType) {
			// Search for complexType
			tmp = getRef(root, "complexType");
			for (int i = 0; i < tmp.length; i++) {
				res.add(tmp[i]);
			}
		}

		// Default types at end
		if (includeSimpleType) {
			// Add primitive types
			String ptypePrefix = "";
			if (root.getPrefix() != null) {
				ptypePrefix = root.getPrefix() + ":";
			}
			for (int i = 0; i < PTYPES.length; i++) {
				res.add(ptypePrefix + PTYPES[i]);
			}
		}
		
		String[] tres = new String[res.size()];
		for (int i = 0; i < res.size(); i++)
			tres[i] = (String) res.get(i);
		return tres;
	}

	public static Element getRef(Element root, String type, String qname) {
		if ( "".equals( qname ) )
			return null;
		String name = qname;
		int j = qname.indexOf( ":" );
		if ( j > -1 ) {
			String prefix = qname.substring( 0, j );
			String namespace = root.lookupNamespaceURI( prefix );
			if ( namespace == null )
				return null;	//?
			// Is it inside the current document ?
			Element n = root.getOwnerDocument().getDocumentElement();
			if ( n.hasAttribute( "targetNamespace" ) ) {
				if ( !n.getAttribute( "targetNamespace" ).equals( namespace ) ) {	// Outside the current namespace
					return null;
				}
			}
			name = qname.substring( j + 1 );
		}

		NodeList children = root.getChildNodes();
		ArrayList res = null;
		for ( int i = 0; i < children.getLength(); i++ ) {
			if (children.item( i ) instanceof Element) {
				Element e = (Element) children.item( i );
				if (type.equals(e.getLocalName()) || 
						"*".equals( type ) ) {
					if ( name.equals( e.getAttribute( "name" ) ) )
						return e;
				}
			}
		}
		return null;
	}
	
	public static void resolveIncludeRedefineImport( String currentPath, Element root ) {
		
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element e = (Element) children.item(i);
				
				if ( isImportation( e ) ) { 
				
					String schemaLocation = e.getAttribute( "schemaLocation" );
					
					Element ee = ( Element )e.getUserData( "parsed" );
					if ( ee == null ) {
						
						XMLFileData xfd = null;
						try {
							// From URL
							if ( schemaLocation.indexOf( "://" ) > -1 )
								xfd = XMLToolkit.getContentFromFileName( new URL( schemaLocation ), null );
							else {
								
								if ( schemaLocation != null ) {
								
									// From PATH
									File parentPath = null;
									if ( currentPath != null ) {
										parentPath = new File( currentPath ).getParentFile();
									}
									
									File goodPath = new File( schemaLocation );
									if ( !goodPath.exists() && parentPath != null ) {	// try relative one
										goodPath = new File( parentPath, schemaLocation );
									}
	
									xfd = XMLToolkit.getContentFromFileName( goodPath.toString(), null );
								
								}
							}
							
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							dbf.setNamespaceAware( true );
							DocumentBuilder db = dbf.newDocumentBuilder();

							Document d = db.parse( new InputSource( new StringReader( xfd.getContent() ) ) );
							if ( d != null ) {
								if ( d.getDocumentElement() != null ) {
									ee = d.getDocumentElement();
									e.setUserData( "parsed", ee, null );
									
									String currentNamespace = ee.getAttribute( "targetNamespace" );
									if( currentNamespace == null )
										currentNamespace = "";

									e.setUserData( 
										"ns", 
										currentNamespace, 
										null 
									);

									// Disabled all									
									NodeList nl = d.getElementsByTagNameNS( SCHEMA_NS, "*" );
									for ( int j = 0; j < nl.getLength(); j++ ) {
										nl.item( j ).setUserData( "disabled", true, null );
									}
									
								}
							}
							
						} catch (MalformedURLException e1) {
						} catch (Throwable e1) {
						}
					}
							
				}
				
			}
		}
		
	}

	private static String[] getRef(Element root, String type) {
		String prefix = "";
		if (root.getAttribute("targetNamespace") != null
				&& !"".equals(root.getAttribute("targetNamespace"))) {
			// We have a namespace
			// Search for a prefix
			NamedNodeMap nnm = root.getAttributes();
			for (int i = 0; i < nnm.getLength(); i++) {
				Node n = nnm.item(i);
				if (n.getNodeName() != null)
					if (n.getNodeName().startsWith("xmlns:")) {
						if (n.getNodeValue().equals(
								root.getAttribute("targetNamespace"))) {
							// We can use this prefix
							prefix = n.getNodeName().substring(6) + ":";
							break;
						}
					}
			}
			if ("".equals(prefix)) {
				// No prefix found, choose one
				prefix = "editix:";
				root.setAttribute("xmlns:editix", root
						.getAttribute("targetNamespace"));
			}
		}

		NodeList children = root.getChildNodes();
		ArrayList res = null;
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element e = (Element) children.item(i);
				
				// Resolved external definition
				if ( "include".equals( e.getLocalName() ) || 
						"redefine".equals( e.getLocalName() ) || 
							"import".equals( e.getLocalName() ) ) {
					
					Element ee = ( Element )e.getUserData( "parsed" );
					if ( ee == null ) {

						if ( e.getUserData( "resolved" ) != null )	// Can't use it
							continue;

						String schemaLocation = e.getAttribute( "schemaLocation" );
						if ( "".equals( schemaLocation ) )
							continue;

						String currentPath = ( String )root.getUserData( "path" );
						XMLFileData xfd = null;
						try {
							// From URL
							if ( schemaLocation.indexOf( "://" ) > -1 )
								xfd = XMLToolkit.getContentFromFileName( new URL( schemaLocation ), null );
							else {
								// From PATH
								File parentPath = null;
								if ( currentPath != null ) {
									parentPath = new File( currentPath ).getParentFile();
								}
								
								File goodPath = new File( schemaLocation );
								if ( !goodPath.exists() && parentPath != null ) {	// try relative one
									goodPath = new File( parentPath, schemaLocation );
								}

								xfd = XMLToolkit.getContentFromFileName( goodPath.toString(), null );
							}

							
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							dbf.setNamespaceAware( true );
							DocumentBuilder db = dbf.newDocumentBuilder();

							Document d = db.parse( new InputSource( new StringReader( xfd.getContent() ) ) );
							if ( d != null ) {
								if ( d.getDocumentElement() != null ) {
									ee = d.getDocumentElement();
									e.setUserData( "parsed", ee, null );
								}
							}
							
						} catch (MalformedURLException e1) {
						} catch (Throwable e1) {
						}
					}

					if ( ee != null ) {
						String[] types = getRef( ee, type );
						if ( types != null ) {
							for ( int j = 0; j < types.length; j++ ) {
								String t = types[ j ];
								int k = t.indexOf( ':' );
								if ( k > -1 ) {	// Skip prefix from included
									t = t.substring( k + 1 );
								}
								if ( res == null )
									res = new ArrayList();
								
								String oldPrefix = prefix;
								
								// For import search the good prefix
								if ( "import".equals( e.getLocalName() ) ) {
									String namespace = e.getAttribute( "namespace" );
									if ( !"".equals( namespace ) ) {
										Element documentElement = e.getOwnerDocument().getDocumentElement();
										String prefixTmp = documentElement.lookupPrefix( namespace );
										if ( prefixTmp == null ) {
											// Create a prefix
											for ( int ii = 1; ii < 50; ii++ ) {
												if ( documentElement.lookupPrefix( "ed" + ii ) == null ) {
													documentElement.setAttribute( "xmlns:ed" + ii, namespace );
													prefix = "ed" + ii + ":"; 
													break;
												}
											}
										} else
											prefix = prefixTmp + ":";
									}
								}

								res.add( prefix + t );
								
								prefix = oldPrefix;
							}
						}
					}

					e.setUserData( "resolved", "ok", null );
				}

				if (type.equals(e.getLocalName())) {
					if (res == null)
						res = new ArrayList();
					if (!"".equals(e.getAttribute("name")))
						res.add(prefix + e.getAttribute("name"));
				}
			}
		}
		if (res == null)
			return new String[] {};
		String[] tmp = new String[res.size()];
		for (int i = 0; i < res.size(); i++)
			tmp[i] = (String) res.get(i);
		return tmp;
	}

	public static Element getElementAtRow(Element parent, int row) {
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				if (row == 0)
					return (Element) nl.item(i);
				row--;
			}
		}
		return null;
	}

	public static Element createTag(Element refElement, String tagName) {
		String pref = refElement.getPrefix();
		String element = tagName;
		if (pref != null)
			element = pref + ":" + element;
		Element e = refElement.getOwnerDocument().createElementNS(
				refElement.getNamespaceURI(), element);
		return e;
	}

	public static Element createElement(Element refElement, String name) {
		String pref = refElement.getPrefix();
		String element = "element";
		if (pref != null)
			element = pref + ":" + element;
		Element e = refElement.getOwnerDocument().createElementNS(
				refElement.getNamespaceURI(), element);
		e.setAttribute("name", name);
		return e;
	}

	public static Element createSimpleType(Element refElement, String name) {
		String pref = refElement.getPrefix();
		String element = "simpleType";
		if (pref != null)
			element = pref + ":" + element;
		Element e = refElement.getOwnerDocument().createElementNS(
				refElement.getNamespaceURI(), element);
		if (name != null)
			e.setAttribute("name", name);
		return e;
	}

	public static String getSimpleType(Element refElement, String type) {
		String pref = refElement.getPrefix();
		if (pref != null)
			return pref + ":" + type;
		else
			return type;
	}

	public static Element createAnyPart(Element refElement, String name,
			String partName) {
		String pref = refElement.getPrefix();
		String element = partName;
		if (pref != null && partName.indexOf(":") == -1)
			element = pref + ":" + element;
		Element e = refElement.getOwnerDocument().createElementNS(
				refElement.getNamespaceURI(), element);
		if (name != null)
			e.setAttribute("name", name);
		return e;
	}

	/*
	 * Content: ((include | import | redefine | annotation)*, (((simpleType |
	 * complexType | group | attributeGroup) | element | attribute | notation),
	 * annotation*)*)
	 */
	public static String[] getParts(Element refElement, int childItem) {
		String pref = refElement.getPrefix();
		if (pref == null)
			pref = "";
		else
			pref = pref + ":";

		ArrayList al = new ArrayList();
		boolean canAddInclude = true;
		for (int i = 0; i < childItem; i++) {
			Element e = getElementAtRow(refElement, i);
			if (!(e.getNodeName().endsWith("include")
					|| e.getNodeName().endsWith("import")
					|| e.getNodeName().endsWith("redefine") || e.getNodeName()
					.endsWith("annotation"))) {
				canAddInclude = false;
				break;
			}
		}
		String[] all = new String[] { 
				"element", "complexType", "simpleType",
				"attribute", "attributeGroup", "group", 
				"include", "redefine", "import", "notation" };
		Collections.addAll(al, all);

		if (canAddInclude) {
			al.add(0, "include");
			al.add(1, "import");
			al.add(2, "redefine");
			al.add(3, "annotation");
		} else {
			NodeList nl = refElement.getChildNodes();
			int cpt = -1;
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i) instanceof Element)
					cpt++;
			}
			if (cpt == childItem) {
				al.add("annotation");
			}
		}
		String[] res = new String[al.size()];
		for (int i = 0; i < al.size(); i++)
			res[i] = (String) al.get(i);
		return res;
	}

	public static Element getRestrictionElement(Element globalDef) {
		Element tmpSimpleType = null;
		if ("element".equals(globalDef.getLocalName())
				|| "attribute".equals(globalDef.getLocalName())) {
			// Search for simpleType
			NodeList nl = globalDef.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if ("simpleType".equals(nl.item(i).getLocalName())) {
					tmpSimpleType = (Element) nl.item(i);
					break;
				}
			}
		} else if ("simpleType".equals(globalDef.getLocalName()))
			tmpSimpleType = globalDef;
		if (tmpSimpleType == null)
			return null;
		NodeList nl = tmpSimpleType.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if ("restriction".equals(nl.item(i).getLocalName())) {
				return (Element) nl.item(i);
			}
		}
		return null;
	}

	public static int getCountForChildren(Element parent, String[] filter) {
		NodeList nl = parent.getChildNodes();
		int cpt = 0;
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				Element _ = (Element) nl.item(i);
				for (int j = 0; j < filter.length; j++) {
					if (filter[j].equals(_.getLocalName())) {
						cpt++;
					}
				}
			}
		}
		return cpt;
	}

	public static boolean hasDOMElementChild(Element ref) {
		NodeList nl = ref.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i) instanceof Element)
				return true;
		return false;
	}

	public static String getTexts(Element parent) {
		StringBuffer sb = new StringBuffer();
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Text) {
				Text t = (Text) nl.item(i);
				sb.append(t.getNodeValue());
			}
		}
		return sb.toString();
	}

	public static Element getChildAt(Element parent, int index, String[] filter) {
		NodeList nl = parent.getChildNodes();
		int cpt = index;
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				Element _ = (Element) nl.item(i);
				for (int j = 0; j < filter.length; j++) {
					if (filter[j].equals(_.getLocalName())) {
						if (cpt == 0)
							return _;
						cpt--;
					}
				}
			}
		}
		return null;
	}

	public static boolean isFacet( Element element ) {
		String name = getElementName( element );
		for ( int i = 0; i < FACETS.length; i++ ) {
			if ( name.equals( FACETS[ i ] ) )
				return true;
		}
		return false;
	}

	public static String getElementName( Node node ) {
		if ( node == null )
			return "?";
		if ( node instanceof Element ) {
			Element element = ( Element )node;
			String name = element.getLocalName();
			if ( name == null )
				name = element.getNodeName();
			return name;
		} else
			return "?";
	}
	
	public static boolean isFirstElement( Element node ) {
		Element parent = ( Element )node.getParentNode();
		if ( parent == null )
			return false;
		NodeList nl = parent.getChildNodes();
		
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {

				// Skip annotation
				if ( "annotation".equals( 
							( ( Element )n ).getLocalName() ) )
						continue;

				if ( n == node )
					return true;
				else
					return false;
			}
		}

		return false;
	}

	public static boolean isLastElement( Element node ) {
		Element parent = ( Element )node.getParentNode();
		if ( parent == null )
			return false;

		NodeList nl = parent.getChildNodes();
		boolean ok = false;
		
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {

				// Skip annotation
				if ( "annotation".equals( 
							( ( Element )n ).getLocalName() ) )
						continue;

				if ( n == node ) {
					ok = true;
				} else
					if ( ok )
						return false;
			}
		}

		return ok;		
	}

	public static int getElementIndex( Element node ) {
		Element parent = ( Element )node.getParentNode();
		if ( parent == null )
			return -1;
		NodeList nl = parent.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) == node )
				return i;
		}
		return -1;
	}

	public static void cleanEmptyTextNode( Node n ) {
		if ( n == null )
			return;
		n = n.getFirstChild();

		while ( n != null ) {
			Node n2 = n;
			n = n.getNextSibling();

			if ( n2.getNodeType() ==
					Node.TEXT_NODE ) {
				String content = n2.getNodeValue();
				boolean empty = true;
				for ( int i = 0; i < content.length(); i++ ) {
					if ( !Character.isWhitespace( content.charAt( i ) ) ) {
						empty = false;
						break;
					}
				}
				if ( empty ) {
					n2.getParentNode().removeChild( n2 );
				}				
			} else
			if ( n2.getNodeType() == Node.ELEMENT_NODE ) {
				Element e = ( Element )n2;
				if ( e.hasChildNodes() ) {
					cleanEmptyTextNode( e );
				}
			}
		}
	}

	public static Element resolveXPathExpression( String xpath, Document d ) {
		StringTokenizer st = new StringTokenizer(
				xpath, 
				"/", 
				false );

		Node r = d.getDocumentElement();
		if ( r == null )
			return null;

		while ( st.hasMoreTokens() ) {
			String t = st.nextToken();
			int i = t.indexOf( '[' );
			String name = t;
			int occ = 1;
			if ( i > -1 ) {
				int j = t.indexOf( ']' );
				if ( j > -1 ) {
					occ = Integer.parseInt( t.substring( i + 1, j ) );
				}
				name = t.substring( 0, i );
			}
			// Search this occurence
			NodeList nl = r.getChildNodes();
			for ( int j = 0; j < nl.getLength(); j++ ) {
				Node n = nl.item( j );
				if ( n.getLocalName() != null && n.getLocalName().equals( name ) ) {
					occ--;
					if ( occ == 0 ) {
						r = n;
						break;
					}
				}
			}
		}
		if ( r instanceof Element )
			return (Element)r;
		else
			return null;
	}

	public static String getXPathExpression( Node e ) {
		StringBuffer sb = new StringBuffer();
		while ( e != null ) {
			
			if ( e instanceof Element ) {
				Node p = e.getParentNode();
				if ( p == null )
					break;
				// Search for the index of e
				NodeList nl = p.getChildNodes();
				int cpt = 0;
				for ( int i = 0; i < nl.getLength(); i++ ) {
					Node n = nl.item( i );
					if ( n instanceof Element ) {
						if ( e.getLocalName() != null && 
								e.getLocalName().equals( n.getLocalName() ) )
								cpt++;
						if ( n == e ) {
							if ( sb.length() > 0 )
								sb.insert( 0, "/" );
							sb.insert( 0, n.getLocalName() + "[" + cpt + "]" );
						}
					}
				}
			} else {
				if ( e instanceof Document ) {
					sb.insert( 0, "/" );			
					break;
				}
			}
			e = e.getParentNode();
		}

		return sb.toString();
	}

	public static boolean canHaveThisChild( Element parentNode, Element childNode ) {
		return canHaveThisChild( parentNode, childNode.getLocalName() );
	}

	public static boolean canHaveThisChild( Element parentNode, String childNodeName ) {
		String[] waitedChildren = getChildrenForElement( parentNode );
		if ( waitedChildren == null )
			return false;
		for ( int i = 0; i < waitedChildren.length; i++ ) {
			if ( waitedChildren[ i ].equals( childNodeName ) ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isPrimitiveType( String type ) {
		return type.startsWith( "xsd:" ) || type.startsWith( "xs:" );
	}
	
}
