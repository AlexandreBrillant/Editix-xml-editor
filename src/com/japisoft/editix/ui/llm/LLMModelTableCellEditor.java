package com.japisoft.editix.ui.llm;

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
