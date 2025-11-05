// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.xml.parser.node.*;

/**
 * Match tag with the good attribute name and value
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
 */
public class AttributeCriteria extends AbstractCriteria {
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
				
				if ( containsMode ) {
					
					for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
						String att = node.getViewAttributeAt( i );

						if ( !ignoreCase ) {						
							if ( att.contains( attribute ) ) {
								return true;
							}
						} else {
							if ( att.toLowerCase().contains( attribute.toLowerCase() ) )
								return true;
						}
					}
					
				} else {
					
					if ( ignoreCase ) {
						
						for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
							String att = node.getViewAttributeAt( i );
							if ( att.equalsIgnoreCase( attribute ) ) 
								return true;
						}
						
						return false;
						
					} else
					
						if ( value == null )
						
							return node.hasAttribute( attribute );
					
						else
							
							return value.equals( node.getAttribute( attribute ) );
				
				}

			} else {
				
				// Recherche de la valeur
				
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					String att = node.getViewAttributeAt( i );
					
					String attValue = node.getAttribute( att );
					
					if ( containsMode ) {
						
						if ( ignoreCase ) {
							
							if ( attValue.toLowerCase().contains( value.toLowerCase() ) )
								return true;
							
						} else {
							
							if ( attValue.contains( value ) )
								return true;
							 
						}
						
					} else {

						if ( ignoreCase ) {
							if ( attValue.equalsIgnoreCase( value ) )
								return true;
						} else {
							if ( attValue.equals( value ) )
								return true;
						}
						
					}
					
				}
			}
			return false;
		} else
				return false;
	}

}


