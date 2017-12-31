package com.japisoft.framework.css;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
public class SimpleParser implements Parser {

	public CSSDocument parse( String baseuri, String uri ) throws Exception {
		URL u = null;
		if ( baseuri == null )
			u = new URL( uri );
		else {
			if ( !baseuri.contains( "://" ) ) {
				baseuri = "file:/" + baseuri;
			}
			u = new URL( new URL( baseuri ), uri );
		}
		String content = getContent( u );
		SimpleCSSDocument doc = new SimpleCSSDocument( content );
		return doc;
	}

	private String getContent( URL u ) throws IOException {
		InputStreamReader r = new InputStreamReader( u.openStream(), "UTF-8" );
		char[] buffer = new char[ 1024 ];
		StringBuffer res = new StringBuffer();
		int c = -1;
		while ( ( c = r.read( buffer ) ) > 0 ) {
			res.append( new String( buffer, 0, c ) );
		}
		return res.toString();
	}

	public static void main( String[] args ) throws Exception {
		SimpleParser p = new SimpleParser();
		CSSDocument doc = p.parse( null, "file:/c:/travail/soft/japisoft-stylededitor/data/Test3.css" );
		Rule r = doc.getRule( "a" );
		Property borderProp = r.getProperty( "border" );
		CSSBorder border = ( CSSBorder )borderProp.getValue();

		String[] dir = new String[] {
			"border-left", "border-right", "border-top", "border-bottom"
		};

		for ( String d: dir ) {
			System.out.println( d + " : " + border.getSide( d ) );
		}
	}

}
