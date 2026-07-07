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
