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

package com.japisoft.framework.wizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
class TitledLabel extends JLabel {

	private int fh = 0;

	public TitledLabel() {
		Font f = getFont();
		setFont( new Font( f.getName(), Font.BOLD, f.getSize() + 2 ) );
		FontMetrics fm = getFontMetrics( getFont() );
		fh = fm.getHeight();
		setBorder( new EmptyBorder( 0, 5, 0, 5 ) );
		setOpaque( false );
	}

	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );
		gc.setColor( Color.GRAY );
		int w = getWidth();
		gc.drawLine( 5, getHeight() - 2, w - 10, getHeight() - 2 );
		//gc.drawLine( 5, getHeight() - 1, w - 10, getHeight() - 1 );
	}

}

