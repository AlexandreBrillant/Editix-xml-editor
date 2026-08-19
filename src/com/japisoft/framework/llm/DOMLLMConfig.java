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

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DOMLLMConfig implements LLMConfig {

	private String name;
	
	public DOMLLMConfig() {
		p = new Properties();
	}
	
	public DOMLLMConfig( Element llm ) {
		this();
		init( llm );
	}

	public void init( Element llm ) {
		name = llm.getAttribute( "name" );
		NodeList nl = llm.getElementsByTagName( "property" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			setProperty( ( ( Element )nl.item( i ) ).getAttribute( "name" ), nl.item( i ).getTextContent() );
		}
	}

	public Element toDOM( Document doc, String type, String name ) {
		Element llm = doc.createElement( "llm" );
		llm.setAttribute( "type", type );
		llm.setAttribute( "name", name );
		if ( p != null ) {
			for ( String key : p.stringPropertyNames() ) {
				String value = p.getProperty( key );
				Element property = doc.createElement( "property" );
				property.setAttribute( "name", key );
				property.setTextContent( value );
				llm.appendChild( property );
			}
		}
		return llm;
	}

	Properties p = null;
	
	@Override
	public String getProperty(String key, String defaultValue) {
		return p.getProperty( key, defaultValue );
	}

	public boolean hasProperty( String key ) {
		return p.containsKey( key );
	}
	
	public void setProperty( String key, String value ) {
		if ( value == null )
			p.remove( key );
		else
			p.setProperty( key, value );					
	}

	public String getName() {
		return name;
	}

	private LLMContext context;
	
	@Override
	public LLMContext getContext() {
		if ( context == null )
			context = new DefaultLLMContext();
		return context;
	}
	
	public void setContext( LLMContext context ) {
		this.context = context;
	}

}
