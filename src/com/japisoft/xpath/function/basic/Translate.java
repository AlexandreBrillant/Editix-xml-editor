// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.


package com.japisoft.xpath.function.basic;

import com.japisoft.xpath.function.*;
import com.japisoft.xpath.XPathContext;
import com.japisoft.xpath.FastVector;

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
public final class Translate extends AbstractFunction {
    public Translate() {
	super();
    }

    public Object eval( XPathContext context, FastVector arg ) {
        if ( arg.size() != 3 )
          throw new RuntimeException( "Invalid usage for translate" );

	java.lang.String s1 = context.getStringValue( arg.elementAt( 2 ) );
	java.lang.String s2 = context.getStringValue( arg.elementAt( 1 ) );
	java.lang.String s3 = context.getStringValue( arg.elementAt( 0 ) );

	char[] c1 = s1.toCharArray();
	char[] c2 = s2.toCharArray();
	char[] c3 = s3.toCharArray();

	for ( int i = 0; i < c2.length; i++ ) {
	    char c = c2[ i ];
	    boolean toRemove = ( i >= ( c3.length - 1 ) );

	    for ( int j = 0; j < c1.length; j++ ) {
		if ( c1[ j ] == c ) {
		    if ( toRemove )
			c1[ j ] = Character.MAX_VALUE;
		    else
			c1[ j ] = c3[ i ];
		}
	    }
	}

	char[] res = new char[ c1.length ];
	int t = 0;

	for ( int i = 0; i < c1.length; i++ ) {
	    if ( c1[ i ] != Character.MAX_VALUE ) {
		res[ t++ ] = c1[ i ];
	    }
	}

	return new java.lang.String( res, 0, t );
    }
}

// Contains ends here
