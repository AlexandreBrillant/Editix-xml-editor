package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.xml.parser.node.*;

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
public class OrCriteria implements ValidCriteria {
	private ValidCriteria criteria1;

	private ValidCriteria criteria2;

	/** Match criteria1 or criteria2 */
	public OrCriteria(ValidCriteria criteria1, ValidCriteria criteria2) {
		super();
		this.criteria1 = criteria1;
		this.criteria2 = criteria2;
	}

	public boolean isValid(FPNode node) {
		return criteria1.isValid( node ) || ( criteria2 != null && criteria2.isValid( node ) );
	}

}

// OrCriteria ends here
