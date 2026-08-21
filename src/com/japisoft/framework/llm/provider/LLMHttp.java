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

package com.japisoft.framework.llm.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import com.japisoft.framework.llm.LLMConfig;

class LLMHttp {

	private final HttpClient client;
	private final String provider;

	LLMHttp( String provider ) {
		this.provider = provider;
		this.client = HttpClient.newHttpClient();
	}

	JSONObject request( String uri, String authHeader, String authValue, String[] headers, JSONObject requestBody ) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri( URI.create( uri ) );
		if ( authHeader != null && authValue != null )
			builder.header( authHeader, authValue );
		if ( headers != null )
			for ( int i = 0; i + 1 < headers.length; i += 2 )
				builder.header( headers[ i ], headers[ i + 1 ] );

		if ( requestBody != null )
			builder.header( "Content-Type", "application/json" )
			.POST( BodyPublishers.ofString( requestBody.toString() ) );
		else
			builder.GET();

		HttpResponse<String> response = client.send(
			builder.build(),
			HttpResponse.BodyHandlers.ofString()
		);

		if ( response.statusCode() >= 400 )
			throw new Exception( provider + " request failed: HTTP " + response.statusCode()
					+ ( authValue != null ? " (API key " + mask( keyOf( authValue ) ) + ")" : "" )
					+ " - " + response.body() );

		return new JSONObject( response.body() );
	}

	static int maxTokens( LLMConfig config ) {
		String maxTokens = config.getProperty( LLMConfig.MAX_TOKENS_PROPERTY, null );
		if ( maxTokens == null )
			return 0;
		try {
			int value = Integer.parseInt( maxTokens.trim() );
			return value > 0 ? value : 0;
		} catch( NumberFormatException exc ) {
			return 0;
		}
	}

	private static String keyOf( String authValue ) {
		if ( authValue.startsWith( "Bearer " ) )
			return authValue.substring( "Bearer ".length() );
		return authValue;
	}

	static String mask( String key ) {
		if ( key == null )
			return "****";
		if ( key.length() <= 8 )
			return "****";
		return key.substring( 0, 4 ) + "****" + key.substring( key.length() - 4 );
	}

}
