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

package com.japisoft.framework.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * A gradient label. It is needed for panel with a title bar
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class GradientLabel extends JComponent {

	private String title = null;
	private Icon icon = null;
	private int height = 0;
	private int ascent = 0;
	private Color gradientStart;
	private Color gradientStop;
	private Color gradientSelectedStart;
	private Color gradientSelectedStop;
	private Color titleColor;

	public GradientLabel() {
		setFont( new Font( "dialog", Font.PLAIN, 12 ) );
		resetColors();
		gradientSelectedStart = UIManager
				.getColor("jdock.innerwindow.gradient.selectedStartColor");
		if (gradientSelectedStart == null)
			gradientSelectedStart = new Color(0, 0, 128);

		gradientSelectedStop = UIManager
				.getColor("jdock.innerwindow.gradient.selectedStopColor");
		if (gradientSelectedStop == null)
			gradientSelectedStop = UIManager.getColor("Panel.background");
	}

	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		if (height == 0) {
			FontMetrics fm = getFontMetrics(getFont());
			height = fm.getHeight() + fm.getHeight() / 2;
			ascent = fm.getDescent();
		}
		if (dim == null)
			dim = new Dimension(1, height);
		else
			dim.height = height;
		return dim;
	}

	public void setTitle(String title) {
		this.title = title;
		repaint();
	}

	public String getTitle() {
		return title;
	}

	public void setIcon( Icon icon ) {
		this.icon = icon;
		repaint();
	}

	public Icon getIcon() {
		return icon;
	}

	private void resetColors() {
		gradientStart = UIManager.getColor( "jdock.innerwindow.gradient.startColor" );
		if (gradientStart == null)
			gradientStart = new Color(128, 128, 128);

		gradientStop = UIManager.getColor( "jdock.innerwindow.gradient.stopColor" );
		
		if (gradientStop == null)
			gradientStop = UIManager.getColor( "Panel.background" );

		titleColor = UIManager.getColor( "jdock.innerwindow.titleColor" );
		if (titleColor == null)
			titleColor = Color.white;	
	}
	
	/** Reset the color for the background header. Use a <code>null</code>
	 * value for restoring the initial value */
	public void setBackground( Color color ) {
		if ( color == null ) {
			resetColors();
		} else {	
			if ( gradientStop == gradientSelectedStart )
				gradientStop = color;
			gradientStart = color;
		}
		repaint();
	}
	
	/** Reset the color for the foreground header. Use a <code>null</code>
	 * value for restoring the initial value */
	public void setForeground( Color color ) {
		if ( color == null )
			resetColors();
		else 
			titleColor = color;
		repaint();
	}

	private boolean focusMode = true;	
	
	public void setFocusMode( boolean focusMode ) {
		this.focusMode = focusMode;
	}
	
	public boolean hasFocusMode() { 
		return focusMode;
	}
	
	public void paintComponent( Graphics gc ) {
		super.paintComponent(gc);
		Graphics2D gc2 = (Graphics2D) gc;

		gc2.setRenderingHint( 
			RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);		
		
		Color start = gradientStart;
		Color stop = gradientStop;

		if (focusMode) {
			start = gradientSelectedStart;
			stop = gradientSelectedStop;
		}

		GradientPaint gradient = new GradientPaint(0, 0, start, getWidth() / 2,
				getHeight(), stop);
		gc2.setPaint(gradient);
		gc2.fillRect(
				0, 
				0, 
				getWidth(), 
				getHeight() );
		gc2.setColor(titleColor);

		if ( icon != null ) {
			icon.paintIcon( this, gc2, 2, ( getHeight() - icon.getIconHeight() ) / 2 );
		}

		if (title != null ) {
			gc2.drawString(title, icon == null ? 10 : icon.getIconWidth() + 5, getHeight() / 2 + ascent + 2);
		}

		// gc2.drawRect(1, 1, getWidth() - 2, getHeight() - 3);
	}
	
}

