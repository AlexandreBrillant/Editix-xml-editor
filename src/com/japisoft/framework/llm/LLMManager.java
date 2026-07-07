// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
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


package com.japisoft.framework.llm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.japisoft.framework.ApplicationModel;

public class LLMManager extends ArrayList<LLM> {

	private static final String LLM_NODENAME = "llm";
	private static final String CONFIG_FILENAME = "llms.xml";
	private static LLMManager instance = null;
	private Document doc;
	
	private LLMManager() {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File userFile = ApplicationModel.getAppFile( CONFIG_FILENAME );
			InputStream stream = null;
			if ( userFile.exists() ) {
				stream = new FileInputStream( userFile );
			} else
				stream = ClassLoader.getSystemResourceAsStream( CONFIG_FILENAME );	
			scanLLM( doc = db.parse( stream ) );
		} catch( Exception exc ) {
			ApplicationModel.debug( exc );
		}
	}

	private void scanLLM( Document doc ) throws Exception {
		NodeList nl = doc.getElementsByTagName( LLM_NODENAME );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element llm = ( Element )nl.item( i );
			add( buildLLMByNode( llm ) );
		}
	}
	
	private LLM buildLLMByNode( Element llm ) throws Exception {
		String type = llm.getAttribute( "type" );
		if ( !"ollama".equalsIgnoreCase( type ) )
			throw new Exception( "Unkown LLM type [" + type + "]?" );
		return new OllamaLLM( llm );
	}

	public static LLMManager instance() {
		if ( instance == null )
			instance = new LLMManager();
		return instance;
	}
	
	public void dump() {
		if ( size() == 0 )
			System.out.println( "No LLM ?" );
		else {
			for ( LLM llm : this )
				llm.dump();
		}
	}

	public void save() throws Exception {
		File userFile = ApplicationModel.getAppFile( CONFIG_FILENAME );
		Transformer t = TransformerFactory.newInstance().newTransformer();
		
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = null;
		doc.appendChild( root = doc.createElement( "llms" ) );
		for ( LLM llm : this ) {
			root.appendChild( llm.toDOM( doc ) );
		}

		t.transform( new DOMSource( doc ), new StreamResult( userFile ));
	}

	
	
	public void newLLM( String name ) throws Exception {
		OllamaLLM newLLM = new OllamaLLM( name );
		add( newLLM );
	}

	public int indexOf( String name ) {
		for ( int i = 0; i < size(); i++ )
			if ( name.equals( get( i ).getName() ) )
				return i;
		return -1;
	}
	
	public boolean renameAt( int index, String newName ) {
		int currentOne = indexOf( newName );
		if ( currentOne == -1 || index == currentOne ) {
			LLM llm = get( index );
			llm.setProperty( "name", newName );
			return true;
		} else
			return false;
	}

	public static void main( String[] args ) throws Exception {
		ApplicationModel.SHORT_APPNAME = "test";
		LLMManager.instance().dump();
		LLM test = LLMManager.instance().get( 0 );
		System.out.println( Arrays.toString( test.models( false ) ) );
		test.setProperty( "model", "ministral-3:3b" );
		System.out.println( test.prompt( "bonjour" ) );
		DefaultLLMContext context = new DefaultLLMContext();
		context.addPromptResponse( "qui suis je ?", "tu es un dieu vivant" );
		test.setContext( context );
		System.out.println( test.prompt( "rappel moi notre dernier échange ?" ) );
		
	}
	
}
