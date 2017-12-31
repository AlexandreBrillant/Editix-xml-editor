package com.japisoft.editix.document.xslt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;

import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

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
public class SaxonExtHandler extends AbstractHelperHandler {

	static Map<String,List<String>> grammar = new HashMap<String,List<String>>();

	static {		
		InputStream input = SaxonExtHandler.class.getResourceAsStream( "saxon.grammar" );
		InputStreamReader r = new InputStreamReader( input );
		BufferedReader br = new BufferedReader( r );
		try {
			String l = null;
			List<String> lst = null;
			while ( ( l = br.readLine() ) != null ) {
				if ( l.startsWith( "*" ) ) {
					grammar.put( l.substring( 1 ), lst = new ArrayList<String>() );
				} else
					lst.add( l );
			}
		} catch( IOException exc ) {
			
		} finally {
			try {
				br.close();
			} catch( Throwable th ) {
			}
		}
	}

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {

		boolean inTag = document.isInsideTag(offset);
		
		if ( "<".equals( addedString ) || ( addedString == null && !inTag ) ) {
			// elements
			List<String> lst = grammar.get( "elements" );
			for ( String l : lst ) {
				TagDescriptor td = new TagDescriptor( l, false );
				td.setAddedPart( addedString );
				addDescriptor( td );
			}
		} else {
			
			if ( document.isInsideAttributeValue( offset ) ) {
				List<String> lst = grammar.get( "functions" );
				for ( String l : lst ) {
					addDescriptor( new BasicDescriptor( l ) );
				}
			} else
				if ( inTag ) {
					List<String> lst = grammar.get( "attributes" );
					for ( String l : lst ) {
						AttDescriptor ad = new AttDescriptor( l, null );
						ad.setAddedPart( " " );
						addDescriptor( ad );
					}
				}			
		}
		
		
		
	}

	protected String getActivatorSequence() {
		return null;
	}

	private boolean isForSaxon( FPNode currentNode ) {
		if ( "http://icl.com/saxon".equals( currentNode.getNameSpaceDeclarationURI( "saxon" ) ) )
			return true;
		if ( currentNode.getFPParent() != null ) {
			return isForSaxon( currentNode.getFPParent() );
		} else
			return false;
	}
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {

		if ( currentNode == null )
			return false;

		return ( null == activatorString ) && isForSaxon( currentNode );
	}

	public String getTitle() {
		return "Saxon extensions";
	}

	public int getPriority() {
		return -1;
	}

}
