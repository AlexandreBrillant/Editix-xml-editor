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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class LLMManager extends ArrayList<AbstractLLM> {

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

	public void moveUp( int row ) {
		if ( row >= 1 && row < size() ) {
			Collections.swap( this, row, row - 1 );
		}
	}

	public void moveDown( int row ) {
		if ( row >= 0 && row < size() - 1 ) {
			Collections.swap( this, row, row + 1 );
		}		
	}

	private void scanLLM( Document doc ) throws Exception {
		NodeList nl = doc.getElementsByTagName( LLM_NODENAME );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element llm = ( Element )nl.item( i );
			add( buildLLMByNode( llm ) );
		}
	}

	private AbstractLLM buildLLMByNode( Element llmConfig ) throws Exception {
		String type = llmConfig.getAttribute( "type" );
		String name = llmConfig.getAttribute( "name" );
		AbstractLLM llm = buildByType( type, new DOMLLMConfig( llmConfig ) );
		llm.setName( name );
		return llm;
	}

	private Map<LLM,String> llmType = null;

	private AbstractLLM buildByType( String type, LLMConfig config ) throws Exception {
		AbstractLLM llm = (AbstractLLM)LLMFactory.instance().newLLM( type );
		llm.init( config );
		if ( llmType == null )
			llmType = new HashMap<LLM, String>();
		llmType.put( llm, type );
		return llm;
	}

	public String getType( LLM llm ) {
		return llmType.get( llm );
	}
	
	public static LLMManager instance() {
		if ( instance == null )
			instance = new LLMManager();
		return instance;
	}
	
	public void save() throws Exception {
		File userFile = ApplicationModel.getAppFile( CONFIG_FILENAME );
		Transformer t = TransformerFactory.newInstance().newTransformer();
		
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = null;
		doc.appendChild( root = doc.createElement( "llms" ) );
		for ( AbstractLLM llm : this ) {
			String type = llmType.get( llm );

			if ( llm instanceof AbstractLLM ) {
				root.appendChild( llm.toDOM( doc, type ) );
			}
		}

		t.transform( new DOMSource( doc ), new StreamResult( userFile ));
	}
	
	public void newLLM( String name, String type ) throws Exception {
		AbstractLLM newLLM = buildByType( type, new DOMLLMConfig() );
		add( newLLM );
		newLLM.setName( name );
	}

	public boolean renameAt( int index, String newName ) {
		AbstractLLM selection = get( index );
		selection.setName( newName );
		return true;
	}	

	public void updateTypeAt( int index, String newType ) throws Exception {
		AbstractLLM oldLLM = get( index ); 
		AbstractLLM newLLM = buildByType( newType, new DOMLLMConfig() );
		newLLM.setName( oldLLM.getName() );		
		newLLM.setProperty( LLMConfig.SYSTEM_PROPERTY, oldLLM.getProperty( LLMConfig.SYSTEM_PROPERTY, null ) );
		set( index, newLLM );
	}

	public int indexOf( String name ) {
		for ( int i = 0; i < size(); i++ )
			if ( name.equals( get( i ).getName() ) )
				return i;
		return -1;
	}



}