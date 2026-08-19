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

	@Override
	public void init(LLMConfig config) {
		super.init(config);
		if ( config instanceof DOMLLMConfig ) {
			// Set a default URL
			DOMLLMConfig dlc = ( DOMLLMConfig )config;
			if ( !dlc.hasProperty( LLMConfig.URL_PROPERTY ) )
				dlc.setProperty( LLMConfig.URL_PROPERTY, DEFAULT_URL );
		}
	}

	@Override
	public String prompt(String request ) throws Exception {
		String model = getProperty( LLMConfig.MODEL_PROPERTY, null );
		if ( model == null )
			throw new Exception( "Can't find a model ?" );
		JSONObject body = new JSONObject();
		String systemPrompt = getProperty( LLMConfig.SYSTEM_PROPERTY, null );

		LLMContext context = getContext();
		
		boolean hasContext = context != null && context.size() > 0;
		
		if ( !hasContext ) {
			if ( systemPrompt != null )
				body.put( "system", systemPrompt );
		}
			
		body.put( "model", model );

		if ( context == null || context.size() == 0 )
			body.put( "prompt", request );
		else {
			org.json.JSONArray array = new org.json.JSONArray();
			
			if ( systemPrompt != null ) {
				org.json.JSONObject objPrompt = new org.json.JSONObject();
				objPrompt.put( "role", "system" );
				objPrompt.put( "content", systemPrompt );			
				array.put( objPrompt );				
			}
			
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
		body.put( "think", "true".equalsIgnoreCase( getProperty( LLMConfig.THINK_PROPERTY, "" ) ) );

		String api = "generate";
		if ( hasContext )
			api = "chat";

		org.json.JSONObject res = request( getUrl( "api/" + api ), body );

		if ( !hasContext )
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
		return getProperty( LLMConfig.URL_PROPERTY, DEFAULT_URL ) + "/" + lastPart;
	}
	
}