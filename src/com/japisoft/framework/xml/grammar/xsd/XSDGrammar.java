package com.japisoft.framework.xml.grammar.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.framework.xml.grammar.Grammar;
import com.japisoft.framework.xml.grammar.GrammarElement;

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
public class XSDGrammar implements Grammar {

	private Document doc = null;
	private String location = null;
	
	public XSDGrammar( String location ) throws Exception {
		this.location = location;
		
		DocumentBuilderFactory dbf = 
			DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware( true );
		dbf.setIgnoringComments( true );
		
		DocumentBuilder db = 
			dbf.newDocumentBuilder();
				
		doc = db.parse( location );
		
		resolveIncludeRedefineImport();
	}

	private void resolveIncludeRedefineImport() {

	}

	private List<GrammarElement> cache = null;
	
	public List<GrammarElement> getGlobalElements() {
		if ( cache == null )
			cache = XSDGrammar.getGlobalComponent( 
				this, 
				doc, 
				"element" 
			);
		return cache;
	}

	public GrammarElement getGlobalElement(String name) {
		List<GrammarElement> l = getGlobalElements();
		if ( l != null ) {
			for ( GrammarElement ge : l ) {
				if ( ge.getName().equalsIgnoreCase( name ) )
					return ge;
			}
		}
		return null;
	}	
	
	public static List<GrammarElement> getGlobalComponent( 
			XSDGrammar grammar, 
			Document doc, 
			String matching ) {
		ArrayList<GrammarElement> 
			al = new ArrayList<GrammarElement>();
		Element root = 
			doc.getDocumentElement();
		NodeList nl = 
			root.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof Element ) {
				String name = n.getNodeName();
				int j = name.indexOf( ":" );
				if ( j > -1 )
					name = name.substring( j + 1 );
				if ( matching.equals( name ) ) {
						al.add(
								new XSDGrammarElement(
									grammar,
									( Element )n 
								)
						);
				}
			}
		}
		return al;
	}

	public String getLocation() {
		return location;
	}

	private String name = null;
	
	public String getName() {
		if ( name == null ) {
			if ( location == null )
				name = "Schema";
			else {
				int i = location.lastIndexOf( "/" );
				if ( i == -1 )
					i = location.lastIndexOf( "\\" );
				if ( i > -1 ) {
					name = location.substring( i + 1 );
				} else
					name = location;
			}
		}
		return name;
	}
	
	public static void main( String[] args ) throws Exception {
		String p = "c:/travail/soft/japisoft-editix-2014/distrib/install-content/samples/xmlform/purchaseOrder.xsd";
		XSDGrammar g = new XSDGrammar( p );
		List<GrammarElement> le = g.getGlobalElements();
		for ( GrammarElement ge : le ) {
			( ( XSDGrammarNode )ge ).dump();
		}
	}

}
