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

package com.japisoft.editix.action.script;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.script.BasicScript;
import com.japisoft.editix.script.Script;
import com.japisoft.editix.script.ScriptModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.toolkit.FileManager;

public class ScriptManagerPanel extends JPanel implements TableModel {

	private JTable t = null;
	
	public ScriptManagerPanel() {
		setLayout( new BorderLayout() );
		JToolBar tb = new JToolBar();
		tb.setFloatable( false );
		tb.add( new AddAction() );
		tb.add( new EditAction() );
		tb.add( new RemoveAction() );
		tb.addSeparator();
		tb.add( new RunAction() );
		
		add( tb, BorderLayout.NORTH );
		add( new JScrollPane( t = new ExportableTable( this ) ) );
	}
	
	// ---------------------------------------------------------------------------------------
	
	private TableModelListener listener = null;
	
	public void addTableModelListener(TableModelListener l) {
		this.listener = l;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int columnIndex) {
		if ( columnIndex == 0 )
			return "Name";
		if ( columnIndex == 1 )
			return "Shortcut";
		return "Path";
	}

	public int getRowCount() {
		return ScriptModel.getInstance().getScripts().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Script s = ScriptModel.getInstance().getScripts().get( rowIndex );
		if ( columnIndex == 0 )
			return s.getName();
		if ( columnIndex == 1 )
			return s.getShortkey();
		return s.getPath();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex <= 1;
	}

	public void removeTableModelListener(TableModelListener l) {
		this.listener = null;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		BasicScript s = ( BasicScript )ScriptModel.getInstance().getScripts().get( rowIndex );
		if ( columnIndex == 0 )
			s.setName( ( String )aValue );
		if ( columnIndex == 1 )
			s.setShortkey( ( String )aValue );
		save();
	}

	private void save() {
		try {
			ScriptModel.getInstance().save();
		} catch( Exception exc ) {
			ApplicationModel.fireApplicationValue( "error", "Can't save the script data" );
			ApplicationModel.debug( exc );
		}		
	}
	
	class AddAction extends AbstractAction {
		public AddAction() {
			putValue( Action.NAME, "New..." );
		}
		public void actionPerformed(ActionEvent e) {
			File path = FileManager.getSelectedFile(true, "js", "JavaScript file (*.js)" );
			if ( path != null ) {
				ScriptModel.getInstance().getScripts().add( new BasicScript( "MyScript", path, "" ) );
				listener.tableChanged( new TableModelEvent(ScriptManagerPanel.this ) );
				save();
			}
		}
	}
	
	class EditAction extends AbstractAction {
		public EditAction() {
			putValue( Action.NAME, "Edit..." );
		}
		public void actionPerformed(ActionEvent e) {
			int row = t.getSelectedRow();
			if ( t.getRowCount() == 1 )
				row = 0;
			if ( row >= 0 ) {
				Script c = ScriptModel.getInstance().getScripts().get( row );
				File path = c.getPath();
				OpenAction.openFile( "JS", false, path, "UTF-8" );
			}
		}
	}
	
	class RemoveAction extends AbstractAction {
		public RemoveAction() {
			putValue(Action.NAME,"Remove" );
		}
		public void actionPerformed(ActionEvent e) {
			int row = t.getSelectedRow();
			if ( row >= 0 ) {
				ScriptModel.getInstance().getScripts().remove( row );
				listener.tableChanged( new TableModelEvent(ScriptManagerPanel.this ) );
				save();
			}
		}
	}

	class RunAction extends AbstractAction {
		public RunAction() {
			putValue( Action.NAME, "Run" );
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			int row = t.getSelectedRow();
			if ( row >= 0 ) {
				Script script = ScriptModel.getInstance().getScripts().get( row );
				TestScript.runScript( e.getSource(), script.getPath() );
			}			
		}
	}
	
}

