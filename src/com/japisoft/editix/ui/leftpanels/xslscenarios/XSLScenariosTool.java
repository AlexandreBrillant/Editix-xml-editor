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

package com.japisoft.editix.ui.leftpanels.xslscenarios;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.NodeFactoryImpl;

public class XSLScenariosTool {

	static FPNode currentRoot;
	
	static FPNode getRoot() throws IOException {
		
		if ( currentRoot != null )
			return currentRoot;

		File f = new File(
				ApplicationModel.getAppUserPath(),
				"xslscenarios.xml" 
		);

		if ( !f.exists() ) {
			return getDefaultRoot();
		} else
			return getRoot( new FileReader( f ) );

	}

	private static FPNode getDefaultRoot() throws IOException {
		
		URL u = XSLScenariosTool.class.getResource( 
				"xslscenarios.xml" 
		);

		if ( u == null ) {
			// Pb with the default snippets ?
			FPNode n = new FPNode( 
					FPNode.TAG_NODE, 
					"group" );
			n.setAttribute( 
					"name", 
					"scenarios" );
			currentRoot = n;
			return n;
		} else {
			return getRoot(
					new InputStreamReader( u.openStream() )
			);
		}
	}

	private static FPNode getRoot( Reader r ) throws IOException {

		try {
			FPParser p = new FPParser();
			Document d = p.parse(r);
			return ( currentRoot = ( FPNode )d.getRoot() );
		} catch (ParseException e) {
			throw new IOException( "Can't parse the styles file : " + e.getMessage() ); 
		}

	}

	public static void storeXSLScenariosFile() throws IOException {

		if ( currentRoot == null )
			return;	//?

		File f = new File(
				ApplicationModel.getAppUserPath(),
				"xslscenarios.xml" );

		Document d = currentRoot.getDocument();
		d.write( new FileWriter( f ) );
		
	}
	
}
