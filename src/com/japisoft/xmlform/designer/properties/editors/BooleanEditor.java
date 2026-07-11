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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import com.japisoft.xmlform.designer.properties.PropertyEditor;
import com.japisoft.xmlform.designer.properties.PropertyEditorListener;

public class BooleanEditor extends JComboBox implements PropertyEditor, ItemListener {

	public BooleanEditor() {
		addItem( "false" );
		addItem( "true" );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		addItemListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeItemListener( this );
	}

	public Object getValue() {
		return "true".equals( 
				getSelectedItem() );
	}

	public Component getView() {
		return this;
	}

	private PropertyEditorListener listener = null;
	
	public void setPropertyEditorListener( PropertyEditorListener listener ) {
		this.listener = listener;
	}

	public void setSelected( boolean selected ) {
	}

	public void setValue(Object value) {
		setSelectedItem( 
			( ( Boolean )value ).booleanValue() ? 
					"true" : "false" );
	}

	public void itemStateChanged(ItemEvent e) {
		if ( listener != null )
			listener.stop();
	}
	
}
