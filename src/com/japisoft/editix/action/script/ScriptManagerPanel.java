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

import com.japisoft.editix.script.BasicScript;
import com.japisoft.editix.script.Script;
import com.japisoft.editix.script.ScriptModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.toolkit.FileManager;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class ScriptManagerPanel extends JPanel implements TableModel {

	private JTable t = null;
	
	public ScriptManagerPanel() {
		setLayout( new BorderLayout() );
		JToolBar tb = new JToolBar();
		tb.setFloatable( false );
		tb.add( new AddAction() );
		tb.add( new RemoveAction() );
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
	
}
