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
import java.util.HashMap;
import java.util.Map;

public class CSSBorder {

	private CSSDim width;
	private String style;
	private Color color;

	public CSSDim getWidth() {
		return width;
	}
	public void setWidth(CSSDim width) {
		this.width = width;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isInvisible() {
		return this.style == null || 
			"none".equalsIgnoreCase( this.style ) || 
					"hidden".equalsIgnoreCase( this.style );
	}

	public void merge( CSSBorder border ) {
		if ( border.width != null )
			this.width = border.width;
		if ( border.style != null )
			this.style = border.style;
		if ( border.color != null )
			this.color = border.color;
	}

	private Map<String,CSSBorder> sides = null;

	/**
	 * @param side border-left, border-right, border-top, border-bottom
	 * @param border
	 */
	public void setSide( String side, CSSBorder border ) {
		if ( sides == null )
			sides = new HashMap<String, CSSBorder>();
		sides.put( side, border );
	}
	
	public CSSBorder getSide( String side ) {
		if ( sides == null )
			return this;
		CSSBorder border = sides.get( side );
		if ( border == null )
			return this;
		return border;
	}
	
	@Override
	public String toString() {
		return this.style + " " + this.width + " " + this.color;
	}
	
}

