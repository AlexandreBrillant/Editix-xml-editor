package com.japisoft.framework.css;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

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
public class SimpleSelector implements Selector {
	
	private int specificity = 1;
	private String mask = null;
	
	public SimpleSelector( String value ) {
		parse( value );
		this.mask = value;
	}

	private int l;

	List<Token> tokens = null;
	
	private void parse( String value ) {
		tokens = new ArrayList<Token>();
		l = 0;
		Token token = null;
		while ( ( token = nextToken( value ) ) != null ) {
			tokens.add( token );
		}		
		specificity = tokens.size();
		if ( tokens.size() == 1 ) {
			if ( "*".equals( tokens.get( 0 ).value ) )
				specificity = 0;
		}
	}

	public void dump() {
		for ( Token t : tokens ) {
			System.out.println( "-token [" + t.value + "]:" + t.type );
		}
	}

	private int lastTokenType = 0;

	private Token nextToken( String value ) {
		String tmp = "";
		int type = 0, i;
		
		if ( lastTokenType == 1 )
			type = 7;
		if ( lastTokenType == 3 )
			type = 8;		

		main : for ( i = l; i < value.length(); i++ ) {
			char c = value.charAt( i );
			if ( !Character.isSpaceChar( c ) ) {
				boolean tokenFound = false;
				
				if ( c == '[' || 
						c == ']' || 
							c == ':' || 
								c == '>' || 
									c == '+' ||
										c == '~' ) {
					if ( !"".equals( tmp ) ) {
						i--;
						break main;
					}
				}
				
				switch( c ) {
					case '[' :
						type = 1;
						tokenFound = true;
						break;
					case ']' :
						type = 2;
						tokenFound = true;
						break;
					case ':' :
						type = 3;
						tokenFound = true;
						break;
					case '>' :
						type = 4;
						tokenFound = true;
						break;
					case '+' :
						type = 5;
						tokenFound = true;
						break;
					case '~' :
						type = 6;
						tokenFound = true;
						break;
				}

				if ( tokenFound ) {
					tmp += c;
					break main;
				} else {
					tmp += c;
				}
			} else {
				if ( !"".equals( tmp ) ) {
					break main;
				}
			}
		}

		if ( "".equals( tmp ) ) {
			return null;
		}

		Token t = new Token();
		t.value = tmp.trim();
		t.type = type;
		lastTokenType = type;
		
		l = i + 1;
		
		return t;
	}

	private boolean equals( String cssName, Element node ) {
		String name = node.getLocalName();
		if ( name == null )
			name = node.getNodeName();
		if ( name.equalsIgnoreCase( 
			cssName ) ) {
			return true;
		} else {
			if ( node.getParentNode() instanceof Element ) {
				return equals( cssName, ( Element )node.getParentNode() );
			} else
				return false;
		}			
	}
	
	public boolean match( Element node ) {

		boolean match = true;

		for ( Token t : tokens ) {
			if ( t.type == 0 ) {
				if ( "*".equals( t.value ) )
					match = match && true;
				else
					match = match && equals( t.value, node ); 
			}
		}

		return match;
	}

	public int getSpecificity() {
		return specificity;
	}

	@Override
	public String toString() {
		return mask;
	} 
	
	public static void main( String[] args ) {
		SimpleSelector ss = new SimpleSelector( "" );
		ss.dump();
	}

}
