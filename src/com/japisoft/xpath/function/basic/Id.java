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
import com.japisoft.xpath.NodeSet;
import java.util.StringTokenizer;
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
public final class Id extends AbstractFunction {
    public Id() {
	super();
    }

    public Object eval( XPathContext context, FastVector arg ) {
	checkOneParameter( arg );
	Object o = arg.elementAt( 0 );

	if ( o instanceof NodeSet ) {
	    NodeSet ns = ( NodeSet )o;
	    NodeSet r = new NodeSet();
	    java.lang.String id = "";
	    for ( int i = 0; i < ns.size(); i++ ) {
		Object o1 = ns.elementAt( i );
		String s = new String();
		FastVector v = new FastVector(1);
		v.addElement( o1 );
		java.lang.String s1 = (java.lang.String)s.eval( context, arg );
		Id id1 = new Id();
		v = new FastVector(1);
		v.addElement( s1 );
		r.union( ( NodeSet )id1.eval( context, v ) );
	    }
	    return r;
	}

	String s = new String();
	java.lang.String id = (java.lang.String)s.eval( context, arg );
	StringTokenizer id2 = new StringTokenizer( id, " \t\n\r", false );
	NodeSet r = new NodeSet();
	while ( id2.hasMoreTokens() ) {
          java.lang.String id1 = id2.nextToken();
          Object node = context.getNodeForId( id1 );
          if ( node != null )
            r.addNode( node );
	}
	return r;
    }

}

// Count ends here
