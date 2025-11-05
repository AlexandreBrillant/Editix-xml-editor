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

package com.japisoft.framework.css;

import java.awt.Color;

public class SimpleProperty implements Property {

	private String name;
	private Object value;
	
	public SimpleProperty( String name, Object value ) {
		this.name = name;
		this.value = value;
	}
	
	public SimpleProperty( String content ) {
		String[] part = content.split( ":" );
		if ( part.length >= 2 ) {
			this.name = part[ 0 ].trim().toLowerCase();
			this.value = part[ 1 ].trim();
			
			if ( name.startsWith("border" ) ) {
				this.value = BorderParser.getInstance().parse( 
					( String )this.value 
				);
			} else
			if ( name.contains( "color" ) ) {
				try {
					this.value = ColorParser.getInstance().parseColor( part[ 1 ] );
					if ( this.value == null )
						this.value = Color.BLACK;
				} catch( Exception exc ) {
					this.value = Color.BLACK;
				}
			} else
			if ( "font-family".equals( name ) ) {
				this.value = FontParser.getInstance().parseFont( part[ 1 ] );
				if ( this.value == null )
					this.value = FontParser.getInstance().getDefaultFont();				
			} else
			if ( "font-size".equals( name ) ) {
				try {
					String unit = null;
					if ( part[ 1 ].endsWith( "px" ) || part[ 1 ].endsWith( "pt" ) || part[ 1 ].endsWith( "em" ) ) {
						unit = part[ 1 ].substring( 
							0, 
							part[ 1 ].length() - 2 
						);
						if ( part[ 1 ].endsWith( "em" ) ) {
							unit = Integer.toString( ( int )Math.round( 12.0f * Float.parseFloat( unit ) ) );
						}
						this.value = Integer.parseInt( unit );
					} else
						this.value = Integer.parseInt( part[ 1 ] );
				} catch( Exception exc ) {
					this.value = null;
				}
			} else
			if ( this.value instanceof String ) {
				if ( ( ( ( String )this.value ) ).endsWith( "%" ) || 
					( ( ( String )this.value ) ).endsWith( "px" ) ) {
					this.value = new CSSDim( ( String )this.value );
				}
			}

		} else
			this.name = "?";
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public static void main( String[] args ) {
		SimpleProperty sp = new SimpleProperty( "font-size:1d3" );
		System.out.println( sp.getValue() );
		
	}

}

