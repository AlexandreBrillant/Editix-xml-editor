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

package com.japisoft.xpath.function.basic;

import com.japisoft.xpath.function.*;
import com.japisoft.xpath.XPathContext;
import com.japisoft.xpath.NodeSet;
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

@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)

*/
public final class Number extends AbstractFunction {
    public Number() {
	super();
    }

    public Object eval( XPathContext context, FastVector arg ) {
	java.lang.String s = null;
	if ( arg.size() == 0 ) {
          s = context.getStringValue( context.getNodeFromContext() );
        } else {
          Object o = arg.elementAt( 0 );
          if ( o instanceof java.lang.Boolean ) {
            java.lang.Boolean b = (java.lang.Boolean) o;
            if (b.booleanValue())
              return new Double(1);
            else
              return new Double(0);
          } else
            if ( o instanceof Double )
              return o;
            else
              if ( o instanceof NodeSet ) {
                NodeSet ns = (NodeSet)o;
                return context.convertNodeSetToDouble( ns );
              } else
                if ( o instanceof java.lang.String ) {
                  s = ( java.lang.String )o;
                }
        }
        try {
          if ( s != null )
            return new Double(s);
          else
            return new Double( Double.NaN );
        } catch( NumberFormatException exc ) {
          return new Double( Double.NaN );
        }
    }

}

