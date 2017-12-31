package com.japisoft.editix.editor.xsd.view.element.simpletype;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.framework.ui.table.ExportableTable;

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
public class UnionViewImpl extends ExportableTable implements View, MouseListener {
	private Element initE;
	private Factory factory = null;	
	private ImageIcon deleteIcon = new ImageIcon( getClass().getResource( "element_delete.png" ) );

	public UnionViewImpl( Factory factory ) {
		this.factory = factory;
		setModel( new UnionModel() );
		getColumnModel().getColumn( 0 ).setCellEditor( new SimpleTypeEditor() );
		getColumnModel().getColumn( 1 ).setCellRenderer( new CustomIconRenderer() );
		getColumnModel().getColumn( 1 ).setMaxWidth( 20 );		
		addMouseListener( this );
	}

	public void init(Element schemaNode) {
		this.initE = schemaNode;
		( ( UnionModel )getModel() ).fireTableDataChanged();
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
		( ( SimpleTypeEditor )getColumnModel().getColumn( 0 ).getCellEditor() ).dispose();
		removeMouseListener( this );
		initE = null;
	}

	public void stopEditing() {
	}

	public void mouseClicked(MouseEvent ev) {
		int row = rowAtPoint( ev.getPoint() );
		int col = columnAtPoint( ev.getPoint() );
		if ( row > -1 && col == 1 ) {
			String element = ( String )getValueAt( row, 0 );
			if ( factory.confirmDialog( "Delete " + element + " ?" ) ) {
				SchemaHelper.deleteUnionType( initE, row );
				( ( UnionModel )getModel() ).fireTableDataChanged();
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
	
	class UnionModel extends AbstractTableModel {
		public int getColumnCount() {
			return 2;
		}
		public String getColumnName(int column) {
			if ( column == 0 )
				return "Item Type";
			else
				return "D";
		}
		public int getRowCount() {
			if ( initE == null )
				return 0;
			else {
				// Search for union element
				String[] qt = SchemaHelper.getUnionTypes( initE );	
				return ( qt.length + 1 );
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			String[] qt = SchemaHelper.getUnionTypes( initE );
			if ( rowIndex < qt.length ) {
				return qt[ rowIndex ];
			} else
				return null;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			boolean mustFire = ( rowIndex == getRowCount() - 1 );
			SchemaHelper.updateUnion( initE, rowIndex, ( String )aValue );
			if ( mustFire ) {
				( ( UnionModel )getModel() ).fireTableDataChanged();
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if ( columnIndex == 0 )
				return true;
			else
				return false;
		}
	}
	
	class CustomIconRenderer extends JButton implements TableCellRenderer {
		public CustomIconRenderer() {
			setIcon( deleteIcon );
		}
		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {
			setEnabled( row != ( getRowCount() - 1 ) );
			return this;
		}
	}

	class SimpleTypeEditor implements TableCellEditor, ActionListener {
		private JComboBox cbb = new JComboBox();		

		SimpleTypeEditor() {
			cbb.setEditable( false );	// Should be true but bug
			cbb.addActionListener( this );
		}

		void dispose() {
			cbb.removeActionListener( this );
		}
		
		public void cancelCellEditing() {}

		public Object getCellEditorValue() {
			return cbb.getSelectedItem();
		}

		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		public boolean stopCellEditing() {
			return true;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			String[] content = SchemaHelper.getType( initE.getOwnerDocument().getDocumentElement(), false, true );
			cbb.removeAllItems();
			for ( int i = 0; i < content.length; i++ ) {
				cbb.addItem( content[ i ] );
			}
			cbb.setSelectedItem( value );
			return cbb;
		}

		public void actionPerformed(ActionEvent e) {
			fireEditingStopped(new ChangeEvent(this));
		}

		private EventListenerList listenerList = new EventListenerList();

		private void fireEditingStopped(ChangeEvent ee) {
			EventListener[] el = listenerList
					.getListeners(CellEditorListener.class);
			if (el != null) {
				for (int i = 0; i < el.length; i++) {
					((CellEditorListener) el[i]).editingStopped(ee);
				}
			}
		}

		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class, l);
		}

		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class, l);
		}
	}

	@Override
	public void cut() {
	}

	@Override
	public void copy() {
	}

	@Override
	public void paste() {
	}

}
