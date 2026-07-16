// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.framework.xml.xpath.function.copy;

import com.japisoft.framework.xml.xpath.FastVector;
import com.japisoft.framework.xml.xpath.NodeSet;
import com.japisoft.framework.xml.xpath.XPathContext;

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

@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)

*/
public abstract class AbstractFunction implements Function {
    public AbstractFunction() {
	super();
    }

    /** Evalute this function for this context and the following arguments */
    public abstract Object eval( XPathContext context, FastVector arg );

    /** Throw a RuntimeException if arg has no single NodeSet argument */
    public void checkOneNumber( FastVector arg ) {
	checkOneParameter( arg );
	if ( !( arg.elementAt( 0 ) instanceof Double ) )
	    throw new RuntimeException( "Invalid parameter in " + this );
    }

    /** Throw a RuntimeException if arg has no single NodeSet argument */
    public void checkOneNodeSet( FastVector arg ) {
	checkOneParameter( arg );
	if ( !( arg.elementAt( 0 ) instanceof NodeSet ) )
	    throw new RuntimeException( "Invalid parameter in " + this );
    }

    /** Throw a RuntimeException if arg has no single argument */
    public void checkOneParameter( FastVector arg ) {
	if ( arg.size() != 1 )
	    throw new RuntimeException( "Invalid parameter in " + this );
    }

    /** Throw a RuntimeException if arg has no 1 string argument */
    public void checkOneString( FastVector arg ) {
	if ( arg.size() != 1 )
	    throw new RuntimeException( "Invalid parameters in " + this );
	if ( !( arg.elementAt( 0 ) instanceof String ) )
	    throw new RuntimeException( "Invalid parameters in " + this );
    }

    /** Throw a RuntimeException if arg has no 2 string arguments */
    public void checkTwoStrings( FastVector arg ) {
	if ( arg.size() != 2 )
	    throw new RuntimeException( "Invalid parameters in " + this );
	if ( !( ( arg.elementAt( 0 ) instanceof String ) &&
		arg.elementAt( 1 ) instanceof String ) )
	    throw new RuntimeException( "Invalid parameters in " + this );
    }

    /** Throw a RuntimeException if arg has no 3 string arguments */
    public void checkThreeStrings( FastVector arg ) {
	if ( arg.size() != 3 )
	    throw new RuntimeException( "Invalid parameters in " + this );
	if ( !( ( arg.elementAt( 0 ) instanceof String ) &&
		( arg.elementAt( 1 ) instanceof String ) &&
		( arg.elementAt( 2 ) instanceof String ) ) )
	    throw new RuntimeException( "Invalid parameters in " + this );
    }

    /** @return only the class name without the package */
    public String toString() {
	return getClass().getName().substring( getClass().getName().lastIndexOf( "." ) );
    }

    public java.lang.String getString1( XPathContext context, FastVector arg ) {
      return context.getStringValue( arg.elementAt( 1 ) );
    }

    public java.lang.String getString2( XPathContext context, FastVector arg ) {
      return context.getStringValue( arg.elementAt( 0 ) );
    }

}

