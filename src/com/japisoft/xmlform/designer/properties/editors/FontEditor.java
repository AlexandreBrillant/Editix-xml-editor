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

package com.japisoft.xmlform.designer.properties.editors;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.japisoft.xmlform.designer.properties.PropertyEditor;
import com.japisoft.xmlform.designer.properties.PropertyEditorListener;

public class FontEditor extends JButton implements 
		PropertyEditor, ActionListener {

	public FontEditor() {
		setText( "Font..." );
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
	
	private Font currentFont = null;
	
	public Object getValue() {
		return currentFont;
	}

	public Component getView() {
		return this;
	}

	public void actionPerformed(ActionEvent e) {
		Font f = FontChooser.showFontChooser( this, "Choose your font", currentFont );
		if ( f == null ) {
			listener.cancel();
		} else {
			currentFont = f;
			listener.stop();
		}
	}

	private PropertyEditorListener listener = null;
	
	public void setPropertyEditorListener(PropertyEditorListener listener) {
		this.listener = listener;
	}

	public void setSelected(boolean selected) {
	}

	public void setValue(Object value) {
		currentFont = ( Font )value;
	}

}

