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

package com.japisoft.framework.xml.xpath.function.basic;

import com.japisoft.framework.xml.xpath.FastVector;
import com.japisoft.framework.xml.xpath.NodeSet;
import com.japisoft.framework.xml.xpath.XPathContext;
import com.japisoft.framework.xml.xpath.function.*;

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
public final class Sum extends AbstractFunction {
    public Sum() {
	super();
    }

    public Object eval( XPathContext context, FastVector arg ) {
	checkOneNodeSet( arg );
	int r = 0;
	NodeSet ns = (NodeSet)arg.elementAt( 0 );
	for ( int i = 0; i < ns.size(); i++ ) {
	    Object node = ns.elementAt( i );
	    java.lang.String s = context.getStringValue( node );
	    Number n = new Number();
	    FastVector v = new FastVector(1);
	    v.addElement( s );
	    Double j = (Double)n.eval( context, v );
	    r += j.intValue();
	}
	return new Double( r );
    }

}

