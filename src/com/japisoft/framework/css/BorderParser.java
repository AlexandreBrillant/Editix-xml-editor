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
import java.util.List;

public class BorderParser {

	private static BorderParser INSTANCE = null;
	
	private static List<String> styles = null;
	
	static {
		styles = new ArrayList<String>();
		styles.add( "none" );
		styles.add( "hidden" );
		styles.add( "dotted" );
		styles.add( "dashed" );
		styles.add( "solid" );
		styles.add( "double" );
		styles.add( "groove" );		
		styles.add( "ridge" );
		styles.add( "inset" );
		styles.add( "outset" );
		styles.add( "inherit" );
	}
	
	private BorderParser() {
		INSTANCE = this;
	}
	
	public static BorderParser getInstance() {
		if ( INSTANCE == null )
			new BorderParser();
		return INSTANCE;
	}
	
	public CSSBorder parse( String content ) {
		CSSBorder border = new CSSBorder();
		String[] parts = content.split( " " );
		for ( String part : parts ) {
			part = part.toLowerCase();
			if ( styles.contains( part ) )
				border.setStyle( part );
			else {
				if ( Character.isDigit(
					part.charAt( 0 ) ) ) {
					border.setWidth( new CSSDim( part ) );
				} else
					border.setColor( 
						ColorParser.getInstance().parseColor( part ) 
					);
			}
				
		}
		return border;
	}
	
}
