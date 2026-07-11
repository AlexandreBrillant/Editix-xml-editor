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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;

public class OllamaLLM extends AbstractLLM {

	private static final String DEFAULT_URL = "http://localhost:11434";
	public static final String TYPE = "ollama";
	
	public OllamaLLM( Element llm ) {
		super( llm );
	}
	
	public OllamaLLM( String name ) {
		super( name, TYPE );
		setProperty( "url", DEFAULT_URL );
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String prompt(String request ) throws Exception {
		String model = getProperty( "model", null );
		if ( model == null )
			throw new Exception( "Can't find a model ?" );
		JSONObject body = new JSONObject();
		String systemPrompt = getProperty( SYSTEM_PROPERTY, null );
		if ( systemPrompt != null )
			body.put( "system", systemPrompt );
		body.put( "model", model );

		LLMContext context = getContext();
		if ( context == null )
			body.put( "prompt", request );
		else {
			org.json.JSONArray array = new org.json.JSONArray();
			for ( LLMExchange exchange : context ) {
				String prompt = exchange.getPrompt();
				String response = exchange.getResponse();
				org.json.JSONObject objPrompt = new org.json.JSONObject();
				objPrompt.put( "role", "user" );
				objPrompt.put( "content", prompt );
				array.put( objPrompt );
				org.json.JSONObject objResponse = new org.json.JSONObject();
				objResponse.put( "role", "assistant" );
				objResponse.put( "content", response );
				array.put( objResponse );
			}
			
			org.json.JSONObject objPrompt = new org.json.JSONObject();
			objPrompt.put( "role", "user" );
			objPrompt.put( "content", request );			
			array.put( objPrompt );
			
			body.put( "messages", array );
		}

		body.put( "stream", false );
		body.put( "think", "true".equalsIgnoreCase( getProperty( THINK_PROPERTY, "" ) ) );

		String api = "generate";
		if ( context != null )
			api = "chat";
		
		org.json.JSONObject res = request( getUrl( "api/" + api ), body );
		
		if ( context == null )
			return res.getString( "response" );
		else {
			org.json.JSONObject message = (org.json.JSONObject)res.get( "message" );
			if ( message == null ) 
				throw new Exception( "No response ?" );
			return message.getString( "content" );
		}
	}

	@Override
	public String[] models( boolean reload ) {
		String cacheModels = getProperty( "models", null );
		if ( reload || cacheModels == null ) {
			try {
				org.json.JSONObject obj = request( getUrl( "api/tags" ) );
				JSONArray array = obj.getJSONArray( "models" );
				List<String> l = new ArrayList<String>();
				for ( int i = 0; i < array.length(); i++ )
					l.add( array.getJSONObject( i ).getString( "name" ) );
				setProperty( "models", String.join( ",", l ) );
				return l.toArray( new String[ l.size() ] );
			} catch( Exception exc ) {
				return null;
			}
		}
		if ( cacheModels != null ) {
			return cacheModels.split( "," );
		}		
		return null;
	}

	private String getUrl( String lastPart ) {
		return getProperty( "url", DEFAULT_URL ) + "/" + lastPart;
	}
	
}