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
public final class SubString extends AbstractFunction {
	public SubString() {
		super();
	}

	public Object eval(XPathContext context, FastVector arg) {
		int narg = arg.size();
		if (narg < 2) {
			throw new RuntimeException("Invalid parameters in " + this);
		} else {
			java.lang.String s = null;
			double p1 = 0;
			double p2 = 0;

			try {
				if (narg == 2) {
					s = (java.lang.String) arg.elementAt(1);
					p1 = ((Double) arg.elementAt(0)).doubleValue();
				} else if (narg == 3) {
					s = (java.lang.String) arg.elementAt(2);
					p1 = ((Double) arg.elementAt(1)).doubleValue();
					p2 = ((Double) arg.elementAt(0)).doubleValue();
				} else {
					return "";
				}

				try {

					if ( p1 <= 0 ) {
						p2 -= ( 1 - p1 );
						p1 = 1; 
					}

					if ( Double.isInfinite( p2 ) ) {
						p2 = s.length(); 
					}

					if (p2 == 0.0)
						return s.substring((int) Math.ceil(p1) - 1);
					else
						return s.substring((int) Math.ceil(p1) - 1, (int) Math
								.ceil(p1 + p2) - 1);

				} catch (IndexOutOfBoundsException exc) {
					return "";
				}

			} catch (ClassCastException exc) {
				throw new RuntimeException(
						"Invalid parameter usage for the substring function");
			}
		}
	}
}
