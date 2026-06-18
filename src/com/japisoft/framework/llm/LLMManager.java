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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LLMManager {

	private static LLMManager instance = null;
	
	private LLMManager() throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		scanLLM( db.parse( ClassLoader.getSystemResourceAsStream( "llms.xml" ) ) );
	}

	private void scanLLM( Document doc ) {
		NodeList nl = doc.getElementsByTagName( "llm" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element llm = ( Element )nl.item( i );
			String type = llm.getAttribute( "type" );
			
		}
	}
	
	public static LLMManager instance() throws Exception {
		if ( instance == null )
			instance = new LLMManager();
		return instance;
	}
	
	public static void main( String[] args ) throws Exception {
		LLMManager.instance();
	}
	
}
