package com.japisoft.editix.editor.xsd.view.element.simpletype;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
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
import javax.swing.event.TableModelEvent;
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
public class RestrictionViewImpl extends ExportableTable implements View, MouseListener {
	private Element initE = null;
	private Factory factory = null;	
	private ImageIcon deleteIcon = new ImageIcon( getClass().getResource( "element_delete.png" ) );

	public RestrictionViewImpl( Factory factory ) {
		this.factory = factory;
		setModel( new RestrictionModel() );
		getColumnModel().getColumn( 2 ).setCellRenderer( new CustomIconRenderer() );
		getColumnModel().getColumn( 2 ).setMaxWidth( 20 );
		getColumnModel().getColumn( 0 ).setCellEditor( new CustomFacetEditor() );
	}

	public void init(Element schemaNode) {
		this.initE = schemaNode;
		( ( RestrictionModel )getModel() ).fireTableDataChanged();
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
		( ( CustomFacetEditor )getColumnModel().getColumn( 0 ).getCellEditor() ).dispose();
		initE = null;
	}

	public void stopEditing() {}

	public void addNotify() {
		super.addNotify();
		addMouseListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeMouseListener( this );
	}	

	public void refreshModel() {
		int selection = getSelectedRow();
		((RestrictionModel)getModel()).fireTableChanged(
				new TableModelEvent( getModel() ) );
		getSelectionModel().setSelectionInterval( selection, selection );
	}
	
	public void mouseClicked(MouseEvent ev) {
		int row = rowAtPoint( ev.getPoint() );
		int col = columnAtPoint( ev.getPoint() );
		if ( row > -1 && col == 2 ) {
			String element = ( String )getValueAt( row, 0 );
			String value = ( String )getValueAt( row, 1 );
			if ( factory.confirmDialog( "Delete " + element + " with value '" + value + "' ?" ) ) {
				Element r = SchemaHelper.getRestrictionElement( initE );
				Element e = SchemaHelper.getChildAt(
						r, row, SchemaHelper.FACETS );
				if ( e != null ) {
					r.removeChild( e );
					refreshModel();
					if ( row > 0 )
						getSelectionModel().setSelectionInterval( row - 1, row - 1 );
					else
						getSelectionModel().setSelectionInterval( 0, 0 );					
				}
			}
		}
	}

	class RestrictionModel extends AbstractTableModel {
		public int getColumnCount() {
			return 3;
		}
		public String getColumnName(int column) {
			if ( column == 0 )
				return "Facet";
			else
			if ( column == 1 )
				return "Value";
			else
				return "D";
		}
		public int getRowCount() {
			if ( initE == null )
				return 0;
			else {
				Element r = SchemaHelper.getRestrictionElement( initE );
				int res = 0;
				if ( r != null ) {
					res = SchemaHelper.getCountForChildren(
							r, SchemaHelper.FACETS );
				}
				return ( res + 1 );
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Element r = SchemaHelper.getRestrictionElement( initE );
			if ( r != null ) {
				Element e = SchemaHelper.getChildAt(
						r, rowIndex, SchemaHelper.FACETS );
				if ( e != null ) {
					if ( columnIndex == 1 )
						return e.getAttribute( "value" );
					else
						return e.getLocalName();
				}
				else
					return null;
			}
			return null;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Element r = SchemaHelper.getRestrictionElement( initE );

			if ( r == null ) {
				Element parent = initE;
				SchemaHelper.removeChildren( initE );
				// Create a restriction component part
				// Create a simpleType ?
				if ( "element".equals( initE.getLocalName() ) || 
						"attribute".equals( initE.getLocalName() ) ) {
					Element st = SchemaHelper.createSimpleType( initE, null );
					initE.appendChild( st );
					parent = st;
				}
				r = SchemaHelper.createTag( initE, "restriction" );
				parent.appendChild( r );
				String type = initE.getAttribute( "type" );
				if ( type == null || "".equals( type ) )
					type = SchemaHelper.getSimpleType( initE, "string" );
				else
					if ( !"".equals( type ) ) {
						// Remove it
						initE.removeAttribute( "type" );
					}
				r.setAttribute( "base", type );
			}

			if ( r != null ) {
				Element ee = SchemaHelper.getChildAt(
						r, rowIndex, SchemaHelper.FACETS );
				if ( ee != null ) {
					if ( columnIndex == 1 )
						ee.setAttribute( "value", ( String )aValue );
				}
				else {
					if ( columnIndex == 0 ) {
						// Add a new facet
						Element facetElement = SchemaHelper.createTag( initE, ( String )aValue );
						r.appendChild( facetElement );
						refreshModel();
					}
				}
			}
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
	}

	class CustomIconRenderer extends JButton implements TableCellRenderer {
		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {
			if ( column == 2 ) {
				setIcon( deleteIcon );
			}
			setEnabled( row != ( getRowCount() - 1 ) );
			return this;
		}
	}

	class CustomFacetEditor implements TableCellEditor, ActionListener {
		private JComboBox cbb = new JComboBox( SchemaHelper.FACETS );		

		CustomFacetEditor() {
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
			if ( anEvent instanceof MouseEvent ) {
				MouseEvent e = ( MouseEvent )anEvent;
				int row = rowAtPoint( e.getPoint() );
				if ( row == getRowCount() -1 )
					return true;
				else
					return false;
			}
			return true;
		}

		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		public boolean stopCellEditing() {
			return true;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
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

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

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
