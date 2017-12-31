package com.japisoft.framework.xml;

import java.util.ArrayList;
import java.util.Stack;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
public class XPathToolkit {

	public static boolean isXPathChild( String parentXPath, String xpath ) {
		
		String[] stepsParent = getXPathSteps( parentXPath );
		String[] stepsChild = getXPathSteps( xpath );
		int j = 0;
		for ( int i = 0; i < stepsChild.length - 1; i++ ) {
			if ( ( stepsParent.length - 1 ) < j )
				return true;
			if ( !stepsChild[ i ].equals( stepsParent[ j++ ] ) )
				return false;
		}
		return ( j == stepsParent.length );

	}

	public static Node buildNode( 
			Document doc, 
			String xpath ) {

		String[] steps = 
			getXPathSteps( xpath );
		Node parentNode = null;
		for ( String step : steps ) {
			
			if ( "/".equals( step ) ) 
				continue;
			
			Node node = null;
			if ( step.contains( "@" ) ) {
				node = 
					doc.createAttributeNS( 
							null, 
							step.substring( 1 ) 
					);
			} else
			if ( step.endsWith( "text()" ) ) {
				node =
					doc.createTextNode( "" );
			} else
				node = doc.createElementNS( 
						null, 
						step );

			if ( parentNode != null ) {
				if ( node instanceof Attr ) {
					if ( parentNode instanceof Element )
						( ( Element )parentNode ).setAttributeNode( ( Attr )node );
				} else
					parentNode.appendChild( node );
			}
			if ( node instanceof Element || 
					parentNode == null )
				parentNode = node;

		}

		return parentNode;
	}

	public static String getRelativeXPath( String fromXPath, String toXPath ) {

		String[] fxsteps = getXPathSteps( fromXPath );
		String[] txsteps = getXPathSteps( toXPath );		

		ArrayList<String> res = new ArrayList<String>();
		int maxCommon = 0;
		for ( int i = 0; i < txsteps.length; i++ ) {
			
			if ( fxsteps.length > i ) {
				String tmp = fxsteps[ i ];
				if ( txsteps[ i ].equals( tmp ) ) {
					// Skip it
					maxCommon = i;
					continue;
				} else
					res.add( ".." );
			}
			
			res.add( txsteps[ i ] );
		}
		
		for ( int j = txsteps.length + 1; j < fxsteps.length; j++ ) 
			res.add(0, ".." );

		StringBuffer sb = new StringBuffer();
		for ( String step : res ) {
			if ( sb.length() > 0 )
				sb.append( "/" );
			sb.append( step );
		}

		if ( res.size() == 0 )
			sb.append( "." );
		
		return sb.toString();
	}

	public static String[] getXPathSteps( String xpath ) {
		ArrayList<String> res = new ArrayList<String>();
		StringBuffer previousStep = new StringBuffer();
		Stack<Boolean> stackPredicates = new Stack<Boolean>(); 
		for ( int i = 0; i < xpath.length(); i++ ) {
			char c = xpath.charAt( i );
			if ( c == '[' ) {
				stackPredicates.push( Boolean.TRUE );
			} else
				if ( c == ']' ) {
					stackPredicates.pop();
				}
			if ( c == '/' ) {
				if ( stackPredicates.isEmpty() ) {
					if ( previousStep.length() == 0 )
						res.add( "/" );
					else {
						res.add( previousStep.toString() );
						previousStep = new StringBuffer();
					}
				}
			} else
				previousStep.append( c );
				
		}
		if ( previousStep.length() > 0 )
			res.add( previousStep.toString() );
		return res.toArray( new String[] {} );
	}
	
	/** @return the main element name from the xpath expression */
	public static String getXPathElementName( String xpath ) {
		String[] content = xpath.split( "/" );
		if ( content == null )
			return xpath;
		String lastPart = content[ content.length - 1 ];
		int i = lastPart.indexOf( "[" );
		if ( i == -1 ) {
			return lastPart;
		}
		return lastPart.substring( 0, i );
	}
	
	public static void main( String[] args ) {
		
		String a = "/a/b/c/d";
		String b = "/a/b/c/d/e";
		System.out.println( isXPathChild( a, b ));

	}
}

