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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultLLMExchange implements LLMExchange {

	private String prompt;
	private String response;
	
	public DefaultLLMExchange( String prompt, String response ) {
		this.prompt = prompt;
		this.response = response;
	}

	public DefaultLLMExchange( Element exchange ) {
		prompt = exchange.getElementsByTagName( "prompt" ).item( 0 ).getTextContent();
		response = exchange.getElementsByTagName( "response" ).item( 0 ).getTextContent();
	}

	@Override
	public String getPrompt() {
		return prompt;
	}

	@Override
	public String getResponse() {
		return response;
	}

	public Element toDOM( Document source ) {
		Element exchange = source.createElement( "exchange" );
		Element eprompt = source.createElement( "prompt" );
		Element eresponse = source.createElement( "response" );
		exchange.appendChild( eprompt );
		exchange.appendChild( eresponse );
		eprompt.setTextContent( prompt );
		eresponse.setTextContent( response );
		return exchange;
	}

}