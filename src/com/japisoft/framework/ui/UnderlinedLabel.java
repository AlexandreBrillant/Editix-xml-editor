package com.japisoft.framework.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

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
public class UnderlinedLabel extends JLabel {

	private int fh = 0;

	public UnderlinedLabel( String text ) {
		this();
		setText( text );
	}

	public UnderlinedLabel() {
		Font f = getFont();
		setFont( new Font( f.getName(), Font.BOLD, f.getSize() + 2 ) );
		FontMetrics fm = getFontMetrics( getFont() );
		fh = fm.getHeight();
		setBorder( new EmptyBorder( 0, 5, 0, 5 ) );
		setOpaque( false );
	}

	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );
		gc.setColor( Color.LIGHT_GRAY );
		int w = getWidth();
		gc.drawLine( 5, getHeight() - 2, w - 10, getHeight() - 2 );
		//gc.drawLine( 5, getHeight() - 1, w - 10, getHeight() - 1 );
	}

}
