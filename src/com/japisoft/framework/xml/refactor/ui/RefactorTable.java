package com.japisoft.framework.xml.refactor.ui;

import com.japisoft.framework.xml.refactor.RefactorManager;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import com.japisoft.framework.xml.refactor.elements.RefactorAction;

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
public class RefactorTable extends JTable {

	private String[] actions;
 // private static int MAX = Preferences.getPreference( "refactor", "maxline", 30 );
    private static int MAX = 30;

	public RefactorTable( String type ) {
		super( 
                    new DefaultTableModel(
                        new String[] { 
                            "Old Value (V1)", 
                            "Action", 
                            "New Value (V2)" }, 
                        MAX ) );

        actions = RefactorManager.getRefactorActions( type );
		getColumnModel().getColumn( 1 ).setCellRenderer( new CustomTableRenderer() );
		getColumnModel().getColumn( 1 ).setCellEditor( new CustomTableEditor() );
		getColumnModel().getColumn( 1 ).setPreferredWidth( 120 );
	}

	public void init( int row, String value ) {
		if ( value != null )
			getModel().setValueAt( value, row, 0 );
	}
	
	private boolean isValidAction( int row ) {
		if ( getValueAt( row, 0 ) != null && 
				getValueAt( row, 1 ) != null ) {
			String action = ( String )getValueAt( row, 1 );
			if ( action.indexOf( "V2" ) > -1 )
				return getValueAt( row, 2 ) != null;
			return true;
		}
		return false;
	}

	public RefactorAction[] getActions() {
		if ( getCellEditor() != null ) 
			getCellEditor().stopCellEditing();
		int cpt = 0;
		for ( int i = 0; i < getRowCount(); i++ ) {
			if ( isValidAction( i ) )
				cpt++;
		}
		if ( cpt == 0 )
			return null;
		RefactorAction[] rat = new RefactorAction[ cpt ];
		cpt = 0;
		for ( int i = 0; i < getRowCount(); i++ ) {
			if ( isValidAction( i ) ) {
				RefactorAction ra = new RefactorAction(
						( String )getValueAt( i, 1 ),
						( String )getValueAt( i, 0 ),
						( String )getValueAt( i, 2 ) );
				rat[ cpt++ ] = ra;
			}
		}
		return rat;
	}

	public void setActions( RefactorAction[] actions ) {
		DefaultTableModel model = ( DefaultTableModel )getModel();
		while ( model.getRowCount() > 0 ) 
			model.removeRow( 0 );
		if ( actions != null )
		for ( int i = 0; i < actions.length; i++ ) {
			model.addRow(
					new Object[] {
							actions[ i ].getOldValue(),
							actions[ i ].getAction(),
							actions[ i ].getNewValue()
					} );
		}
	}

	class CustomTableRenderer implements TableCellRenderer {
		private JLabel cb = new JLabel();
		
		public CustomTableRenderer() {
			cb.setOpaque( true );
		}
		
		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {			
			cb.setText( ( String )value );
			if ( isSelected ) {
				cb.setBackground( table.getSelectionBackground() );
				cb.setForeground( table.getSelectionForeground() );
			} else {
				cb.setBackground( table.getBackground() );
				cb.setForeground( table.getForeground() );
			}
			return cb;
		}
	}

	class CustomTableEditor implements TableCellEditor,ItemListener,MouseListener {
		private JComboBox cb = new JComboBox( actions );

		CustomTableEditor() {
			cb.addItemListener( this );
			addMouseListener( this );
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {			
			cb.setSelectedItem( ( String )value );
			return cb;
		}
		CellEditorListener l;
		public void addCellEditorListener(CellEditorListener l) {
			this.l = l;
		}
		public void itemStateChanged(ItemEvent e) {
			if ( l != null )
				l.editingStopped( null );	
		}
		public void mouseClicked(MouseEvent e) {
			itemStateChanged( null );
		}		
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void cancelCellEditing() {
		}
		public Object getCellEditorValue() {
			return cb.getSelectedItem();
		}
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}
		public void removeCellEditorListener(CellEditorListener l) {
		}
		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}
		public boolean stopCellEditing() {
			return false;
		}
	}
}
