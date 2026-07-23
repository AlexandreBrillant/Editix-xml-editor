package com.japisoft.framework.llm;

import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LLMToolkit {

	public static SimpleEntry<String,String> extractTypeContent( String llmResponse ) {
		String regex = "```(?<type>\\w+)\\s*\\n(?<content>.*?)```";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher( llmResponse );
		if ( matcher.find() ) {
			String response_doc_type = matcher.group("type").trim();
			String response_doc_content = matcher.group("content").trim();		
			return new SimpleEntry<String,String>( response_doc_type, response_doc_content );
		}		
		return null;
	}

}
