package com.japisoft.framework.llm;

import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;

import com.japisoft.framework.llm.provider.OllamaLLM;

public class LLMFactory {

	private static LLMFactory INSTANCE = null;
	
	private LLMFactory() {
		addLLM( OllamaLLM.TYPE, OllamaLLM.class );
	}

	public static LLMFactory instance() {
		if ( INSTANCE == null )
			INSTANCE = new LLMFactory();
		return INSTANCE;
	}

	private Map<String,Class<? extends LLM>> classes = null;

	public void addLLM( String type, Class<? extends LLM> llm ) {
		if ( classes == null )
			classes = new HashMap<String, Class<? extends LLM>>();
		classes.put( type, llm );
	}

	public LLM newLLM( String type ) throws Exception {
		if ( classes == null )
			throw new Exception( "No LLM definition ?" );
		Class<? extends LLM> llm = classes.get( type );
		if ( llm == null )
			throw new Exception( "Can't find the LLM of type [" + type + "] ?" );
		return llm.newInstance();
	}

	private String[] types = null; 
	
	public int size() {
		if ( classes == null )
			return 0;
		return classes.size();
	}

	public String[] getTypes() {
		if ( types == null ) {
			String[] tmp =  ( classes.keySet() ).toArray( new String[0] );
			Arrays.sort( tmp );
			types = tmp;
		}
		return types;
	}

}
