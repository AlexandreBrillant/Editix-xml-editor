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

package com.japisoft.xmlform.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.plaf.ColorUIResource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.xmlform.component.container.XMLFormContainer;
import com.japisoft.xmlform.designer.data.GrammarNodeTreeNode;
import com.japisoft.xmlform.editor.EditorModel;

public class XMLDeserizalizer {

	public static AbstractXMLFormComponent build( 
			Document doc, 
			boolean editing,
			GrammarNodeTreeNode parentNode,
			HashMap<String,AbstractXMLFormComponent> components,
			ComponentContext context ) throws Exception {
		Element root = 
			doc.getDocumentElement();
		
		if ( root.hasAttribute( "spell") ) {
			EditorModel.SPELL_CHECK = root.getAttribute( "spell" );
		} else
			EditorModel.SPELL_CHECK = null;

		NodeList nl = 
			root.getElementsByTagName( "fields" );
		if ( nl.getLength() == 0 )
			return null;
		Element fields = 
			( Element )nl.item( 0 );

		AbstractXMLFormComponent result = null;
		
		NodeList children = fields.getChildNodes();
		for ( int i = 0; i < children.getLength(); i++ ) {
			if ( children.item( i ) instanceof Element ) {
				Element e = 
					( Element )children.item( i );
				if ( "field".equals( 
						e.getNodeName() ) ) {
					result = build( 
							e, 
							editing, 
							parentNode, 
							components, 
							context, 
							true );
					break;
				}
			}
		}

		if ( result == null )
			throw new Exception( 
				"Invalid format, can't find fields" );

		if ( root.hasAttribute( "schema" ) ) {
			String schemaURI = root.getAttribute( "schema" );
			if ( result instanceof XMLFormContainer ) {
				( ( XMLFormContainer )result ).setSchemaURI( schemaURI );
			}
		}

		return result;

	}

	public static AbstractXMLFormComponent build( 
			Element field, 
			boolean editing,
			GrammarNodeTreeNode parentNode,
			HashMap<String,AbstractXMLFormComponent> components,
			ComponentContext context,
			boolean firstComponent ) throws Exception {

		String cl = field.getAttribute( "class" );
		// Read properties
		HashMap<String,Object> properties = 
			getProperties( field );
		AbstractXMLFormComponent component = 
			context.getComponentFactory().newComponent( 
					cl,
					properties,
					editing,
					context );

		component.setReferenceComponent( true );
		
		if ( components != null )
			components.put( 
					component.getId(), 
					component );

		// Resolve parentNode
		if ( ( parentNode != null ) && 
				( component.getXpath() != null ) ) {
			GrammarNodeTreeNode childNode = parentNode.resolveRelativeXPath(
					component.getXpath() );
			if ( childNode != null ) {
				component.setGrammarNode( childNode );
			}
			parentNode = childNode;
		}

		component.setFieldDescription( field );
		component.setTopComponent( firstComponent );
		// Check for inner children
		NodeList children = field.getChildNodes();
		for ( int i = 0; i < children.getLength(); i++ ) {
			Node n = children.item( i );
			if ( n instanceof Element ) {
				Element e = ( Element )n;
				if ( "field".equals( 
						e.getNodeName() ) ) {
					component.add(
							build( 
								e, 
								editing, 
								parentNode, 
								components,
								context, 
								false ) );
				}
			}
		}

		return component;
	}

	public static boolean isPropertyManaged( Object value ) {
		if ( value instanceof String )
			return true;
		if ( value instanceof Rectangle )
			return true;
		if ( value instanceof Color )
			return true;
		if ( value instanceof ColorUIResource ) 
			return true;
		if ( value instanceof Boolean )
			return true;
		if ( value instanceof Integer )
			return true;
		if ( value instanceof Font )
			return true;
		if ( value instanceof String[] )
			return true;
		if ( value instanceof HashMap )
			return true;
		return false;
	}

	private static HashMap<String,Object> getProperties( Element field ) throws Exception {
		Element properties = null;
		NodeList children = field.getChildNodes();
		for ( int i = 0; i < children.getLength(); i++ ) {
			if ( children.item( i ) instanceof Element ) {
				Element e = ( Element )children.item( i );
				if ( "properties".equals( e.getNodeName() ) ) {
					properties = e;
					break;
				}
			}
		}
		if ( properties == null )
			throw new Exception( "Invalid format, can't find properties" );
		children = 
			properties.getElementsByTagName( "property" );
		HashMap<String,Object> res = new HashMap<String,Object>();
		for ( int i = 0; i < children.getLength(); i++ ) {
			Element p = ( Element )children.item( i );
			String cl = p.getAttribute( "class" );
			String v = p.getAttribute( "value" );
			String n = p.getAttribute( "name" );
			
			if ( "java.lang.String".equals( cl ) ) {
				res.put( n, v );
			} else
			if ( "java.lang.Boolean".equals( cl ) ) {
				res.put( n, new Boolean( v ) );
			} else
			if ( "java.lang.Integer".equals( cl ) ) {
				res.put( n, new Integer( v ) );
			}
			if ( "java.awt.Rectangle".equals( cl ) ) {
				String[] r = v.split( "," );
				res.put( n, new Rectangle( 
						Integer.parseInt( r[ 0 ] ),
						Integer.parseInt( r[ 1 ] ),
						Integer.parseInt( r[ 2 ] ),
						Integer.parseInt( r[ 3 ] ) ) );
			} else
			if ( "java.lang.Boolean".equals( cl ) ) {
				res.put( n, "true".equals( v ) );
			} else
			if ( "java.awt.Color".equals( cl ) || 
					"javax.swing.plaf.ColorUIResource".equals( cl ) ) {
				String[] r = v.split( "," );
				res.put( n, new Color( 
						Integer.parseInt( r[ 0 ] ),
						Integer.parseInt( r[ 1 ] ),
						Integer.parseInt( r[ 2 ] ) ) );
			} else
			if ( "java.awt.Font".equals( cl ) ) {
				String[] r = v.split( "," );
				res.put( n, new Font( 
						r[ 0 ], 
						Integer.parseInt( r[ 1 ] ), 
						Integer.parseInt( r[ 2 ] ) ) );
			} else
			if( "[Ljava.lang.String;".equals( cl ) ) {
				String[] r = v.split( "~~" );
				res.put( n, r );
			} else
			if ( "java.util.HashMap".equals( cl ) ){
				String[] r = v.split( "~~" );
				HashMap<String,String> map = new HashMap<String, String>();
				for ( int j = 0; j < r.length; j += 2 ) {
					map.put( r[ j ], r[ j + 1 ] );
				}
				res.put( n, map );
			}
		}
		return res;
	}
	
}

