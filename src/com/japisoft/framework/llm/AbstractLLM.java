// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.llm;

import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractLLM implements LLM {

	AbstractLLM( Element llm ) {
		NodeList nl = llm.getElementsByTagName( "property" );
		for ( int i = 0; i < nl.getLength(); i++ ) {
			setProperty( ( ( Element )nl.item( i ) ).getAttribute( "name" ), nl.item( i ).getTextContent() );
		}
	}

	Properties p = null;
	
	@Override
	public String getProperty(String key, String defaultValue) {
		if ( p == null ) return defaultValue;
		return p.getProperty( key, defaultValue );
	}

	public void setProperty( String key, String value ) {
		if ( p == null )
			p = new Properties();
		p.setProperty( key, value );
	}
	
}
