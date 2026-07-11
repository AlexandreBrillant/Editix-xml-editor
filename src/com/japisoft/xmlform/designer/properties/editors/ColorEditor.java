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

package com.japisoft.xmlform.designer.properties.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import com.japisoft.xmlform.designer.properties.PropertyEditor;
import com.japisoft.xmlform.designer.properties.PropertyEditorListener;

public class ColorEditor extends JButton implements PropertyEditor, ActionListener {

	private Color color = null;
	
	public ColorEditor() {
		setIcon( new ColorIcon() );
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeActionListener( this );
	}
	
	public Object getValue() {
		return color;
	}

	public Component getView() {
		return this;
	}

	PropertyEditorListener l;
	
	public void setPropertyEditorListener(PropertyEditorListener listener) {
		this.l = listener;
	}

	public void setSelected(boolean selected) {
	}

	public void setValue(Object value) {
		this.color = ( Color )value;
		repaint();
	}

	public void actionPerformed(ActionEvent e) {
		Color tmp = JColorChooser.showDialog( this, "Select a color", color );
		if ( tmp != null ) {
			this.color = tmp;
			l.stop();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////


	class ColorIcon implements Icon {
		public int getIconHeight() {
			return 16;
		}
		public int getIconWidth() {
			return 16;
		}
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if ( color != null ) {
				g.setColor( color );
				g.fillRect( x, y, 16, 16 );
			}
		}
	}

}
