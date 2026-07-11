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

package com.japisoft.framework.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

public class SimpleRule implements Rule {

	private List<Property> properties = null;
	private Map<String,Property> map = null;

	public SimpleRule() {
		super();
	}

	public SimpleRule( Rule r ) {
		this();
		addProperties( r.getProperties() );
	}

	private void addProperties( List<Property> l ) {
		if ( l != null ) {
			for ( Property p : l ) {
				if ( getProperty( p.getName() ) == null )	// Don't override
					addProperty( p );
			}
		}		
	}

	private String[] borderDirections = 
		new String[] {
			"border-top", "border-right", "border-left", "border-bottom"
	};

	public void removeProperty(String name) {
		name = name.toLowerCase();
		if ( map != null ) {
			Property p = map.get( name );
			if ( p != null ) {
				properties.remove( p );
				map.remove( name );
			}
		}
	}
	
	public void addProperty( Property property ) {
		if ( properties == null ) {
			properties = new ArrayList<Property>();
		}
		properties.add( property );
		if ( map == null ) {
			map = new HashMap<String, Property>();
		}
		map.put( 
			property.getName().toLowerCase(), 
			property
		);
		for ( String borderDir : borderDirections ) {
			// Border part
			if ( property.getName().startsWith( borderDir + "-" ) ) {
				Property borderProperty = map.get( borderDir );
				if ( borderProperty == null ) {
					borderProperty = new SimpleProperty( borderDir, new CSSBorder() );
					addProperty( borderProperty );
				}
				( ( CSSBorder )borderProperty.getValue() ).merge( ( CSSBorder )property.getValue() );
			} else {
				// Full border
				if ( property.getName().equals( borderDir ) ) {
					Property borderProp = map.get( "border" );
					CSSBorder border = null;
					if ( borderProp == null ) {
						borderProp = new SimpleProperty( "border", border = new CSSBorder() );
						addProperty( borderProp );
					} else
						border = ( CSSBorder )borderProp.getValue();
					border.setSide( property.getName(), ( CSSBorder )property.getValue() );
				}
			}
		}
	}

	public List<Property> getProperties() {
		return properties;
	}
	
	/**
	 * r is ancestor, low priority
	 */
	public Rule merge( Rule r ) {
		SimpleRule sr = new SimpleRule( this );
		sr.addProperties( r.getProperties() );
		return sr;
	}

	public Property getProperty( String name ) {
		if ( map == null )
			return null;
		return map.get( name );
	}

	private Selector selector;
	
	public void setSelector( Selector selector ) {
		this.selector = selector;
	}

	public Selector getSelector() {
		return selector;
	}
	
	public boolean match(Element node) {
		if ( selector != null )
			return selector.match( node );
		return false;
	}

}
