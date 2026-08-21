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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.net.http.HttpResponse;

public abstract class AbstractLLM implements LLM {
	
	public AbstractLLM() {
	}

	private LLMConfig config;
	
	@Override
	public void init(LLMConfig config) {
		this.config = config;
	}

	public LLMConfig getConfig() {
		return this.config;
	}
	
	private String name;
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getProperty( String key, String defaultValue ) {
		return config.getProperty(key, defaultValue );
	}
	
	public Element toDOM( Document doc, String type ) {
		if ( config instanceof DOMLLMConfig ) {
			DOMLLMConfig dlc = ( DOMLLMConfig )config;
			return dlc.toDOM( doc, type, getName() );
		} else
			return null;
	}

	public void setProperty( String key, String value ) {
		if ( config instanceof DOMLLMConfig ) {
			( ( DOMLLMConfig )config ).setProperty( key, value );
		} else
			throw new RuntimeException( "Bad config ?" );
	}
	
	public int getMaxTokens() {
		String maxTokens = getProperty( LLMConfig.MAX_TOKENS_PROPERTY, null );
		if ( maxTokens == null )
			return 0;
		try {
			return Integer.parseInt( maxTokens );
		} catch( NumberFormatException exc ) {
			return 0;
		}
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

	protected LLMContext getContext() {
		return config.getContext();
	}
	
	public void setContext( LLMContext context ) {
		if ( config instanceof DOMLLMConfig ) {
			( (DOMLLMConfig)config ).setContext( context );
		}
	}

}