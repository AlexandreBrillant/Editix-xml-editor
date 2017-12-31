package com.japisoft.xmlpad.editor.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

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
public class ExpressionUnderlineHighlighter implements HighlightPainter {

	private Color color;
	
	public ExpressionUnderlineHighlighter( 
			Color color  ) {
		this.color = color;
	}

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			Rectangle r0 = c.modelToView( p0 );
			Rectangle r1 = c.modelToView( p1 );
			g.setColor( color );
			g.drawLine( 
				r0.x, 
				r0.y + r0.height, 
				r1.x, 
				r0.y + r0.height 
			);
		} catch( BadLocationException exc ) {			
		}		
	}	

}
