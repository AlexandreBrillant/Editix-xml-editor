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
public final class NamespaceURI extends AbstractFunction {
    public NamespaceURI() {
	super();
    }

    public Object eval( XPathContext context, FastVector arg ) {
	if ( arg.size() > 0 &&
	     arg.elementAt( 0 ) instanceof NodeSet ) {
	    NodeSet ns = ( NodeSet )arg.elementAt( 0 );
	    if ( ns.size() == 0 )
		return "";
	    java.lang.String uri = context.getNamespaceURI( ns.elementAt( 0 ) );
	    if ( uri == null )
		return "";
	    return uri;
	}
        if ( context.getNodeFromContext() == null )
          return "";
	java.lang.String uri = (java.lang.String)context.getNamespaceURI( context.getNodeFromContext() );
        if ( uri == null )
          return "";
        return uri;
    }

}

// Count ends here
