package com.japisoft.framework.llm;

import java.util.Date;

public class TestLLM extends AbstractLLM {

	@Override
	public String prompt(String request) throws Exception {
		return request + " at " + new Date();
	}

	@Override
	public String[] models(boolean reload) throws Exception {
		return new String[] { "test1", "test2" };
	}

}
