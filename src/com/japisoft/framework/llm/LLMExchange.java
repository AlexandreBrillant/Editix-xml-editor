package com.japisoft.framework.llm;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface LLMExchange {

	public String getPrompt();
	public String getResponse();
	Element toDOM( Document source );
	
}
