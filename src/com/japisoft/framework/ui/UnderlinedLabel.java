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

package com.japisoft.framework.ui;

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
