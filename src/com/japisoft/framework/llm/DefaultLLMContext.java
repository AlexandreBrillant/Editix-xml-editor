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

package com.japisoft.framework.llm;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DefaultLLMContext extends ArrayList<LLMExchange> implements LLMContext {
	
	public DefaultLLMContext() {
		super();
	}

	public DefaultLLMContext( File source ) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
		Document doc = db.parse( source );
		init( doc );
	}

	private void init( Document doc ) {
		Element root = doc.getDocumentElement();
		NodeList nl = root.getElementsByTagName( "exchange" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element exchange = ( Element )nl.item( i );
			add( new DefaultLLMExchange( exchange ) );
		}
	}

	public void addPromptResponse( String prompt, String response ) {
		add( new DefaultLLMExchange( prompt, response ) );
	}

	public void save( File output ) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
		Document document = db.newDocument();
		Element root = null;
		document.appendChild( root = document.createElement( "exchange" ) );
		for ( LLMExchange exchange : this ) {
			root.appendChild( exchange.toDOM( document ) );
		}		
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty( OutputKeys.INDENT, "yes" );
		t.transform( 
			new DOMSource( document ),
			new StreamResult( output )
		);
	}
	
}