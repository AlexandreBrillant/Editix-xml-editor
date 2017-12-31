// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.


package com.japisoft.xpath.function.basic;

import com.japisoft.xpath.function.Lib;
import com.japisoft.xpath.function.Function;
import com.japisoft.xpath.XPathContext;
import com.japisoft.xpath.FastVector;
import java.util.Hashtable;

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
public final class FunctionLib extends Hashtable implements Lib {

    public FunctionLib() {
	super();
	put( "last", Last.class );
	put( "position", Position.class );
	put( "count", Count.class );
	put( "id", Id.class );
	put( "local-name", LocalName.class );
	put( "namespace-uri", NamespaceURI.class );
	put( "name", Name.class );
	put( "string", String.class );
	put( "concat", Concat.class );
	put( "starts-with", StartsWith.class );
	put( "contains", Contains.class );
	put( "substring-before", SubStringBefore.class );
	put( "substring-after", SubStringAfter.class );
	put( "substring", SubString.class );
	put( "string-length", StringLength.class );
	put( "normalize-space", NormalizeSpace.class );
	put( "translate", Translate.class );
	put( "boolean", Boolean.class );
	put( "not", Not.class );
	put( "true", True.class );
	put( "false", False.class );
	put( "lang", Lang.class );
	put( "number", Number.class );
	put( "sum", Sum.class );
	put( "floor", Floor.class );
	put( "ceiling", Ceiling.class );
	put( "round", Round.class );
    }

    private Hashtable htResolved = null;

    /**
     * Eval the function by its name. If the function is
     * unknown a runtime exception is thrown.
     *
     * @param function a <code>String</code> value
     * @param context a <code>XPathContext</code> value
     * @param arg a <code>Vector</code> value
     * @return an <code>Object</code> value
     */
    public Object eval( java.lang.String function, XPathContext context, FastVector arg ) {
	if ( htResolved == null )
	    htResolved = new Hashtable();
	Function f = ( Function )htResolved.get( function );
	if ( f == null ) {
	    Class c = ( Class )get( function );
	    if ( c == null )
		throw new RuntimeException( "Unknown function " + function );
	    try {
		f = ( Function )c.newInstance();
		htResolved.put( function, f );
	    } catch( Throwable th ) {
		throw new RuntimeException( "Can't use " + function );
	    }
	}
	return f.eval( context, arg );
    }

}

// FunctionLib ends here
