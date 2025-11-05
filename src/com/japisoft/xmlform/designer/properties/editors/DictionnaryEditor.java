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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;

import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.xmlform.designer.properties.PropertyEditor;
import com.japisoft.xmlform.designer.properties.PropertyEditorListener;
import com.japisoft.xmlform.editor.EditorModel;

public class DictionnaryEditor 
		extends JButton implements 
			ActionListener, PropertyEditor {
	
	public DictionnaryEditor() {
		setText( "Dictionnary" );
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
		return value;
	}

	public Component getView() {
		return this;
	}

	private PropertyEditorListener listener;
	
	public void setPropertyEditorListener( PropertyEditorListener listener ) {
		this.listener = listener;
	}

	public void setSelected(boolean selected) {
	}

	private HashMap<String,String> value = null;
	
	public void setValue( Object value ) {
		this.value = ( HashMap )value;
	}

	public void actionPerformed(ActionEvent e) {
		DictionnaryPanel dp = new DictionnaryPanel();
		dp.init( ( HashMap )value );

		if ( DialogManager.showDialog(
			EditorModel.MAIN_FRAME,
			"Values",
			"Values",
			"Set your visible value",
			null,
			dp ) == DialogManager.OK_ID ) {

			dp.stopEditing();
			listener.stop();

		} else

			listener.cancel();
	}	

}

