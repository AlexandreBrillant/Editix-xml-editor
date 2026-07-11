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

package com.japisoft.editix.ui.llm.config;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMManager;

public class LLMModelTableCellEditor extends JComboBox<String> implements TableCellEditor, ActionListener {

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
	
	@Override
	public Object getCellEditorValue() {	
		return getSelectedItem();
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		l.editingStopped(null);	
		return true;
	}

	@Override
	public void cancelCellEditing() {
		l.editingStopped(null);
	}

	CellEditorListener l;
	
	@Override
	public void addCellEditorListener(CellEditorListener l) {
		this.l = l;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		this.l = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		stopCellEditing();
	}

	private void initValues( int row ) {
		LLM llm = LLMManager.instance().get( row );
		try {
			setModel( new DefaultComboBoxModel( llm.models( false ) ) );
		} catch( Exception exc ) {
			setModel( new DefaultComboBoxModel( new String[] {} ) );
			if ( l != null )
				l.editingCanceled(null);
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		initValues( row );
		setSelectedItem( value );
		return this;
	}

}