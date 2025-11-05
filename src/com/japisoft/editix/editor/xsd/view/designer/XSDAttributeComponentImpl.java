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

package com.japisoft.editix.editor.xsd.view.designer;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

public class XSDAttributeComponentImpl extends XSDSimpleComponentImpl {

	public XSDAttributeComponentImpl() {
		paintElementName = false;
		hasOpenIcon = false;
	}

	protected Shape createBorderShape() {
		int openWidth = OPEN_ICON.getIconWidth();
		int openHeight = OPEN_ICON.getIconHeight();
		int y = 1;
		int x = 1;
		if ( !hasOpenIcon ) {
			openWidth = 0;
			openHeight = 0;
		}
		return new RoundRectangle2D.Double( (double)x, (double)y, (double)( getWidth() - openWidth - 2 ),
				(double)( getHeight() - 2 ), 10.0, 10.0 );
	}
	
	protected boolean isOptional() {
		return !e.hasAttribute( "use" ) || 
			"optional".equals( e.getAttribute( "use" ) );
	}	
	
}

