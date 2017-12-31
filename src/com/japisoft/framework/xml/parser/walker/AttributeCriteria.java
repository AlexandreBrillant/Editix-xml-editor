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
public class AttributeCriteria implements ValidCriteria {
	private String attribute;
	private String value;

	/** Any attribute name */
	public static String ANY_ATTRIBUTE = "*";

	/** Match the following attribute */
	public AttributeCriteria( String attribute ) {
		super();
		this.attribute = attribute;
		if (attribute == null)
			throw new RuntimeException( "Invalid criteria attribute" );
	}

	/** Match the following attribute and value. If you want to ignore the attribute name,
	 * use the ANY_ATTRIBUTE name */
	public AttributeCriteria( String attribute, String value ) {
		super();
		this.attribute = attribute;
		this.value = value;
		ignoreAttribute = ( ANY_ATTRIBUTE.equals( attribute ) );
		if (attribute == null || value == null)
			throw new RuntimeException( "Invalid criteria attribute name & value" );
	}

	private boolean namespace = false;

	public AttributeCriteria( String attribute, String value, boolean namespace ) {
		this( attribute, value );
		this.namespace = namespace;
		if ( namespace ) {
			if ( value != null ) {
				int i = value.indexOf( ":" );
				if ( i > -1 ) {
					try {
						this.value = value.substring( i + 1 );
					} catch( IndexOutOfBoundsException exc ) {
					}
				}
			}
		}
	}

	private boolean ignoreAttribute = false;

	public boolean isValid( FPNode node ) {
		if ( node.isTag() ) {
			if ( !ignoreAttribute ) {
				if ( value == null ) {
					return node.hasAttribute( attribute );
				}
				String attVal = node.getAttribute( attribute );
				if ( attVal == null )
					return false;
				boolean res = ( value.equals( attVal ) );
				if ( !res ) {
					if ( namespace ) {
						int i = attVal.indexOf( ":" );
						if ( i > -1 ) {
							try {
								attVal = attVal.substring( i + 1 );
								return value.equals( attVal );
							} catch( IndexOutOfBoundsException exc ) {
								return false;
							}
						}
					} else
						return false;
				} else
					return true;
			} else {
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					String att = node.getViewAttributeAt( i );
					if ( value.equals(node.getAttribute( att ) ) )
						return true;
				}
			}
			return false;
		} else
				return false;
	}

}

// AttributeCriteria ends here
