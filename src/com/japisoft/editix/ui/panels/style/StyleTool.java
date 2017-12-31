package com.japisoft.editix.ui.panels.style;

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

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class StyleTool {

	static FPNode currentRoot;
	
	static FPNode getRoot() throws IOException {
		
		if ( currentRoot != null )
			return currentRoot;

		File f = new File(
				ApplicationModel.getAppUserPath(),
				"styles.xml" );
		if ( !f.exists() ) {

			return getDefaultRoot();
			
		} else
			return getRoot( new FileReader( f ) );

	}

	private static FPNode getDefaultRoot() throws IOException {
		
		URL u = StyleTool.class.getResource( "styles.xml" );
		if ( u == null ) {
			// Pb with the default snippets ?
			FPNode n = new FPNode( 
					FPNode.TAG_NODE, 
					"group" );
			n.setAttribute( 
					"name", 
					"styles" );
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

	public static void storeSnippetsFile() throws IOException {

		if ( currentRoot == null )
			return;	//?

		File f = new File(
				ApplicationModel.getAppUserPath(),
				"styles.xml" );

		Document d = currentRoot.getDocument();
		d.write( new FileWriter( f ) );
		
	}
	
}
