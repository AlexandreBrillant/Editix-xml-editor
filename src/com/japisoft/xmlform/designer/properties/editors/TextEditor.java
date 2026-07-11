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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import com.japisoft.xmlform.designer.properties.PropertyEditor;
import com.japisoft.xmlform.designer.properties.PropertyEditorListener;

public class TextEditor extends JTextField 
		implements 
			PropertyEditor, 
			ActionListener {

	public static final int STRING_TYPE = 0;
	public static final int INT_TYPE = 1;
	
	private int type = STRING_TYPE;

	public TextEditor() {
		super();
	}

	public TextEditor( int type ) {
		this();
		this.type = type;
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
	
	public void actionPerformed(ActionEvent e) {
		listener.stop();
	}

	public Object getValue() {
		if ( type == INT_TYPE ) {
			try {
				return Integer.parseInt( 
						getText() );
			} catch ( NumberFormatException e ) {
				listener.cancel();
				return 0;
			}
		} else		
			return getText();
	}

	public Component getView() {
		return this;
	}

	private PropertyEditorListener listener = null;
	
	public void setPropertyEditorListener(PropertyEditorListener listener) {
		this.listener = listener;
	}

	public void setSelected( boolean selected ) {
	}

	public void setValue( Object value ) {
		if ( type == INT_TYPE ) {
			setText( value.toString() );
		} else
		setText( (String)value );
	}

}
