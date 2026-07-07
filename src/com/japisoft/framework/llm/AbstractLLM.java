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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractLLM implements LLM {

	private String name;
	private String type;
	
	AbstractLLM( Element llm ) {
		name = llm.getAttribute( "name" );
		type = llm.getAttribute( "type" );
		NodeList nl = llm.getElementsByTagName( "property" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			setProperty( ( ( Element )nl.item( i ) ).getAttribute( "name" ), nl.item( i ).getTextContent() );
		}
	}

	AbstractLLM( String name, String type ) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public Element toDOM(Document doc) {
		Element llm = doc.createElement( "llm" );
		llm.setAttribute( "name", name );
		llm.setAttribute( "type", type );
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
		if ( p == null ) return defaultValue;
		return p.getProperty( key, defaultValue );
	}

	public void setProperty( String key, String value ) {
		if ( "name".equals( key ) ) {
			name = value;
			return;
		}
		if ( p == null )
			p = new Properties();
		p.setProperty( key, value );
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	protected org.json.JSONObject request( String uri ) throws Exception {
		return request( uri, null );
	}

	protected org.json.JSONObject request( String uri, org.json.JSONObject requestBody ) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = null;

		if ( requestBody != null ) {
			request = HttpRequest.newBuilder().uri( URI.create( uri ) ).header( "Content-Type", "application/json" ).POST( 
					BodyPublishers.ofString( requestBody.toString() ) ).build();
		} else {
			request = HttpRequest.newBuilder().uri( URI.create( uri ) ).GET().build();
		}

		HttpResponse<String> response = client.send(
			request,
			HttpResponse.BodyHandlers.ofString()
		);

		String result = response.body();
		return new org.json.JSONObject( result );
	}

	@Override
	public void dump() {
		System.out.println( "- LLM " + type + " [" + name + "] : " );
		if ( p == null )
			System.out.println( "No properties ?" );
		else {
			try {
				p.store( System.out, "Properties" );
			} catch( IOException exc ) {
				exc.printStackTrace();
			}
		}
	}

	private LLMContext context;
	
	@Override
	public void setContext(LLMContext context) {
		this.context = context;
	}
	
	protected LLMContext getContext() {
		return context;
	}
	

}
