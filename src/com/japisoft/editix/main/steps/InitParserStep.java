// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
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

package com.japisoft.editix.main.steps;

import java.io.StringReader;

import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.xmlpad.tree.parser.XMLParserFactory;

/**
 * Force parsing initialization for avoiding latency for the first document
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class InitParserStep extends ApplicationStepAdapter {

	@Override
	public void start(String[] args) {
		super.start(args);
		try {
			XMLParserFactory.getInstance().newParser().parse( new StringReader( "<a/>"), null );
		} catch( Throwable th ) {
			System.err.println( "Can't parse a document with the default parser ?" );
			th.printStackTrace();
		}
	}
	
}

