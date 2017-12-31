package com.japisoft.xflows.task;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xflows.XFlowsApplicationModel;
import com.japisoft.xflows.task.ui.builder.TaskTypeListener;

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
public class TaskTable extends JTable implements ListSelectionListener {

	private TableCellEditor editor = null;
	private TableCellRenderer renderer = null;
	private TaskTypeListener listener = null;
	private boolean editable;
	
	public TaskTable( TaskTypeListener listener, boolean editable ) {
		super();
		this.editable = editable;
		this.listener = listener;
		setModel( new TableModel() );
		getColumnModel().getColumn( 0 ).setMaxWidth( 40 );
		getColumnModel().getColumn( 2 ).setMaxWidth( 90 );
		getColumnModel().getColumn( 2 ).setPreferredWidth( 90 );
		getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	}

	public boolean isCellEditable(int row, int column) {
		if ( column == 0 )
			return false;
		return editable;
	}
	
	public void stopEditing() {
		if  ( editor != null )
			editor.stopCellEditing();
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged( e );
		if  (getModel() instanceof TableModel ) {	
			if ( getSelectedRow() == -1 )
				return;
			Task t = getCurrentTask();
			if ( listener != null ) {				
				listener.showUI( t );
			}
		}
		repaint();
	}

	public Task getCurrentTask() {
		return ( Task )( (TableModel)getModel() ).innerModel.get(
					getSelectedRow() );
	}
	
	public TableCellEditor getDefaultEditor( Class columnClass ) {
		if ( editor != null )
			return editor;
		editor = new TableEditor();
		return editor;
	}
	
	public TableCellRenderer getDefaultRenderer( Class columnClass ) {
		if ( renderer != null )
			return renderer;
		renderer = new TableRenderer();
		return renderer;
	}

	public void setTasks( List<Task> tasks ) {
		( ( TableModel )getModel() ).resetModel( tasks );
		if ( tasks.size() > 0 )
			getSelectionModel().setSelectionInterval(0,0);
	}

	public List<Task> getTasks() {
		return ( ( TableModel )getModel() ).innerModel;
	}

	public void upTask( ) {
		if  ( editor != null )
			editor.stopCellEditing();
		
		TableModel model = ( TableModel )getModel();
		int row = getSelectedRow();
		if ( row > 0 ) {
			Task t = ( Task )model.innerModel.get( row );
			if ( t != null ) {
				model.innerModel.remove( t );
				model.innerModel.add( row - 1, t );
				model.fireTableDataChanged();
			}
		}
	}

	public void downTask() {
		if  ( editor != null )
			editor.stopCellEditing();
		
		TableModel model = ( TableModel)getModel();
		int row = getSelectedRow();
		if ( row < model.getRowCount() - 1 && row != -1 ) {
			Task t = ( Task )model.innerModel.get( row );
			if ( t != null ) {
				model.innerModel.remove( t );
				model.innerModel.add( row + 1, t );
				model.fireTableDataChanged();
			}
		}
	}

	public void deleteTask() {
		if  ( editor != null )
			editor.stopCellEditing();
		TableModel model = ( TableModel)getModel();
		int row = getSelectedRow();
		if ( row == -1 )
			return;
		model.innerModel.set( row, null );
		model.fireTableDataChanged();
	}

	public void runTask() {
		if ( editor != null )
			editor.stopCellEditing();	
		if ( getSelectedRow() != -1 ) {
			TaskManager.stopIt = false;
			TaskManager.run( ( Task )( ( TableModel )getModel() ).innerModel.get(
					getSelectedRow() ), TaskManager.BACKGROUND_DIALOG );
		}
	}

	public void selectTaskForName( String name ) {
		for ( int i = 0; i < getRowCount(); i++ ) {
			if ( name.equals( getValueAt( i, 1 ) ) ) {
				getSelectionModel().setSelectionInterval( i, i );
				break;
			}
		}
	}
	
	///////////////////////////////////

	Color first = Preferences.getPreference( "interface", "tableFirstColor", new Color( 220, 255, 220 ) );
	Color second = Preferences.getPreference( "interface", "tableSecondColor", new Color( 255, 255, 255 ) );
		
	class TableRenderer implements TableCellRenderer {

		JLabel lbl = new JLabel();
		JComboBox combo = new JComboBox( TaskElementFactory.getAvailableTypes() );
		
		public TableRenderer() {
			lbl.setOpaque( true );
			combo.insertItemAt( "No Action", 0 );
		}
		
		public Component getTableCellRendererComponent(
				JTable table, 
				Object value,
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {

			Component c = lbl;
			if ( column == 2 )
				c = combo;

			if ( c != combo || !ApplicationModel.isMacOSXPlatform() ) {
			
				if ( row % 2 == 0 ) {
					c.setForeground( Color.black );
					c.setBackground( first );
				} else { 
					c.setForeground( Color.black );
					c.setBackground( second );
				}

				if ( isSelected ) {
					Color _tmp = c.getForeground();
					c.setForeground ( c.getBackground() );
					c.setBackground( _tmp );
				}
				
			}

			if ( c == lbl  ) {
			
				if ( value != null ) {
					lbl.setText( value.toString() );
				}
				else {
					lbl.setText( null );
				} 
		
				return lbl;

			} else {
			
				if ( value != null ) {
					
					combo.setSelectedItem( value.toString() );
					
				} else {
					
					combo.setSelectedItem( "No Action" );
					
				}
				
				return combo;
			}

		}
	}

	class TableEditor extends AbstractCellEditor 
			implements TableCellEditor, ItemListener {

		private JTextField name = new JTextField();
		private JComboBox type = new JComboBox( TaskElementFactory.getAvailableTypes() );
		private JComponent lastCompo = null;
		private int lastRow = -1;
		private int lastCol = -1;

		public TableEditor() {
			super();
			type.addItemListener( this );
		}
		
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {

			this.lastRow = row;
			this.lastCol = column;

			JComponent c = lastCompo;
			
			if ( column == 1 ) {
				if ( value != null )
					name.setText( value.toString() );
				else
					name.setText( null );
				c = name;
			} else
			if ( column == 2 ) {
				if ( value != null )
					type.setSelectedItem( value.toString() );
				else
					type.setSelectedIndex( -1 );	// No selection
				c = type;
			}

			if ( c != null && ( c!= type || !ApplicationModel.isMacOSXPlatform() ) ) {
			
				if ( row % 2 == 0 ) {
					c.setForeground( Color.black );
					c.setBackground( first );
				} else { 
					c.setForeground( Color.black );
					c.setBackground( second );
				}

			}
			
			lastCompo = c;
			
			return lastCompo;
		}

		public void actionPerformed(ActionEvent e) {
		}
		
		public void itemStateChanged(ItemEvent e) {
			if ( lastRow != -1 && 
					lastCol == 2 ) {
				Task t = 
					( Task )( ( TableModel )getModel() ).innerModel.get( lastRow );

				( ( TableModel )getModel() ).setValueAt( type.getSelectedItem(), lastRow, lastCol );
				t = ( Task )( ( TableModel )getModel() ).innerModel.get( lastRow );					
				
				listener.showUI( t );				
			}			
		}
		
		public Object getCellEditorValue() {
			if ( lastCompo == null )
				return null;
			if ( lastCompo instanceof JTextField ) {
				return ( ( JTextField )lastCompo ).getText();
			} else
			if ( lastCompo instanceof JComboBox ) {
				return ( ( JComboBox )lastCompo ).getSelectedItem();
			}
			return null;
		}

		protected void fireEditingStopped() {
			if ( lastRow != -1 && 
					lastCol == 2 ) {
				Task t = 
					( Task )( ( TableModel )getModel() ).innerModel.get( lastRow );
				if ( t != null ) { 
					listener.showUI( t );
				}
			}
			super.fireEditingStopped();
		}
	}

	class TableModel extends AbstractTableModel {
		
		List<Task> innerModel = null;

		public TableModel() {
			int rc = getRowCount();
			innerModel = new ArrayList<Task>( rc );
			for ( int i = 0; i < rc; i++ )
				innerModel.add( null );
		}

		void resetModel( List<Task> model ) {
			this.innerModel = model;
			if ( model.size() < getRowCount() ) {
				for ( int i = model.size(); i < getRowCount(); i++ )
					model.add( null );
			}
			getSelectionModel().removeListSelectionListener( TaskTable.this );
			fireTableDataChanged();
			getSelectionModel().addListSelectionListener( TaskTable.this );
		}

		public int getRowCount() {
			return Preferences.getPreference( "xflows", "maxtask", 30 );
		}
		
		public int getColumnCount() {
			return 3;
		}

		public String getColumnName(int column) {
			if  ( column == 0 )
				return "Order";
			else
			if ( column == 1 )
				return "Name";
			return "Action";
		}

		public Object getValueAt( int rowIndex, int columnIndex ) {
			if  ( columnIndex == 0 ) { 
				return new Integer( rowIndex + 1 );
			} else {
				Task t = ( Task )innerModel.get( rowIndex );
				if ( t != null ) {
					if ( columnIndex == 1 )
						return t.getName();
					if ( columnIndex == 2 )
						return t.getType();
				}
			}
			return null;
		}

		public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
			Task t = ( Task )innerModel.get( rowIndex );
			if ( t == null ) {
				t = new Task();
				innerModel.set( rowIndex, t );
			}
			if ( columnIndex == 1 ) {
				t.setName( ( String ) aValue );
			} else
			if ( columnIndex == 2 ) {
				t.setType( ( String ) aValue );
				if ( aValue != null && 
						( t.getName() == null || "".equals( t.getName() ) ) ) {
					// Reset a name
					int cpt = 1;
					for ( int i = 0; i < getRowCount(); i++ ) {
						if ( getValueAt( i, 1 ) != null ) {
							if ( aValue.equals( getValueAt( i, 2 ) ) ) {
								cpt++;
							}
						}
					}
					setValueAt( aValue.toString() + "_" + cpt, rowIndex, 1 );
				}
			}
			XFlowsApplicationModel.setModified();			
			fireTableCellUpdated( rowIndex, columnIndex );
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if ( columnIndex == 0 )
				return false;
			return true;
		}		
	}

}
