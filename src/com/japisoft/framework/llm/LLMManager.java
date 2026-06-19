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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LLMManager {

	private static LLMManager instance = null;
	private List<LLM> llms = null;
	
	private LLMManager() throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		scanLLM( db.parse( ClassLoader.getSystemResourceAsStream( "llms.xml" ) ) );
	}

	private void scanLLM( Document doc ) throws Exception {
		NodeList nl = doc.getElementsByTagName( "llm" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element llm = ( Element )nl.item( i );
			if ( llms == null )
				llms = new ArrayList<LLM>();
			llms.add( getLLMByNode( llm ) );
		}
	}
	
	private LLM getLLMByNode( Element llm ) throws Exception {
		String type = llm.getAttribute( "type" );
		if ( !"ollama".equalsIgnoreCase( type ) )
			throw new Exception( "Unkown LLM type [" + type + "]?" );
		return new OllamaLLM( llm );
	}

	public static LLMManager instance() throws Exception {
		if ( instance == null )
			instance = new LLMManager();
		return instance;
	}
	
	public void dump() {
		if ( llms == null )
			System.out.println( "No LLM ?" );
		else {
			for ( LLM llm : llms )
				llm.dump();
		}
	}
	
	public int size() { 
		if ( llms == null )
			return 0;
		return llms.size();
	}

	public LLM llmAt( int index ) {
		return llms.get( index );
	}
	
	public static void main( String[] args ) throws Exception {
		LLMManager.instance().dump();
		LLM test = LLMManager.instance().llmAt( 0 );
		System.out.println( Arrays.toString( test.models( false ) ) );
		System.out.println( Arrays.toString( test.models( false ) ) );
		test.setProperty( "model", "ministral-3:3b" );
		System.out.println( test.prompt( null, "bonjour" ) );
	}
	
}
