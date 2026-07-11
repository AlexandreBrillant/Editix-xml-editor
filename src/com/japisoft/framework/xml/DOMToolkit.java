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

package com.japisoft.framework.xml;

import java.io.FileInputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.templates.OutputProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.japisoft.framework.xml.format.Formatter;

public class DOMToolkit {

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	public static Element getFirstElement( 
			Element parent, 
			String matching ) {
		NodeList nl = parent.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				if ( matching.equals( ( ( Element )nl.item( i ) ).getLocalName() ) ) {
					return ( Element )nl.item( i );
				}
			}
		}
		return null;
	}

	public static Document parse(
			String content, 
			String location ) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource( new StringReader( content ) );
		if ( location != null ) {
			is.setSystemId( location );
		}
		return db.parse( is );
	}
	
	public static Document parse(
		boolean lightMode,
		String location ) throws Exception {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		if ( lightMode ) {
			dbf.setNamespaceAware( true );	
			dbf.setXIncludeAware( false );
			dbf.setValidating( false );
			dbf.setFeature( "http://xml.org/sax/features/validation", false );
			dbf.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
		}
		
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource( new FileInputStream( location ) );
		if ( location != null ) {
			is.setSystemId( location );
		}
		return db.parse( is );
	}
	
	public static String DOM2String( 
			Node n, 
			int indentSize,
			boolean autoProlog ) throws Exception {
		String tmp = Formatter.format( n );
		if ( autoProlog && !tmp.contains( "<?xml" ) ) {
			tmp = "<?xml version ='1.0'?>\n\n" + tmp;
		}
		return tmp;
	}
	
	public static void save( String filePath, Node node ) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		transformer.transform( new DOMSource( node ), new StreamResult( filePath ) );
	}
	
	public static Node[] toArray( NodeList nl ) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		for ( int i = 0; i < nl.getLength(); i++ )
			nodes.add( nl.item( i ) );
		return nodes.toArray( new Node[ nodes.size() ] );
	}
	
}

