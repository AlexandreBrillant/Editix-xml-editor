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

public interface LLM {

	public static final String SYSTEM_PROPERTY = "system";
	public static final String THINK_PROPERTY = "think";
	
	public String getName();
	public String getType();
	public void setProperty( String key, String value );
	String getProperty( String key, String defaultValue );
	String prompt( String request ) throws Exception;
	String[] models( boolean reload ) throws Exception;
	public void dump();
	public Element toDOM( Document doc );
	public void setContext( LLMContext context );
	
}