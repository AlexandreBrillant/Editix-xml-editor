package com.japisoft.editix.action.dtdschema;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.japisoft.editix.action.dtdschema.generator.MetaAttribute;
import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.MetaObject;
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
public class MetaModelUpdatePanel extends JPanel implements TreeSelectionListener {

	private JTree tree = null;
	private ExportableTable table = null;
	private DefaultTableModel model = null;	
	private JCheckBox cbSequence = null;

	public MetaModelUpdatePanel( MetaNode root ) {
		super();
		setLayout( new BorderLayout() );

		JSplitPane sp = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT );

		sp.setLeftComponent( getMetaModelComponent( root ) );
		sp.setRightComponent( getAttributeComponent() );
		
		add( sp );
		
		add( cbSequence = new JCheckBox( "Default sequence", true ), BorderLayout.SOUTH );
		
		cbSequence.setToolTipText( "If selected, it forces a sequence by default, otherwise it tries to guess" );
	}

	public boolean hasDefaultSequence() {
		return cbSequence.isSelected();
	}

	public void addNotify() {
		super.addNotify();
		tree.addTreeSelectionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		tree.removeTreeSelectionListener( this );
	}

	private JComponent getMetaModelComponent( MetaNode root ) {
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(
				tree = new JTree( root ) );		
		tree.setShowsRootHandles( false );
		return sp;
	}

	private JComponent getAttributeComponent() {
		model = 
			new DefaultTableModel(
					new String[] { "Attribute name", "Attribute type" }, 0 );
		table = new ExportableTable() {
			public boolean isCellEditable(int row, int column) {
				if ( column == 0 )
					return false;
				return true;
			}
		};

		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );

		table.setModel( model );
		
		table.getColumnModel().getColumn( 1 ).setCellRenderer(
				new DefaultRenderer() );
		table.getColumnModel().getColumn( 1 ).setCellEditor(
				new DefaultEditor() );
		
		return new JScrollPane( table );
	}

	public void valueChanged( TreeSelectionEvent e ) {
		model = 
			new DefaultTableModel(
					new String[] { "Attribute name", "Attribute type" }, 0 );

		table.setModel( model );
		
		table.getColumnModel().getColumn( 1 ).setCellRenderer(
				new DefaultRenderer() );
		table.getColumnModel().getColumn( 1 ).setCellEditor(
				new DefaultEditor() );
		
		MetaNode node = ( MetaNode )tree.getSelectionPath().getLastPathComponent();
		Vector v = node.getAttributes();
		if ( v != null ) {
			for ( int i = 0; i < v.size(); i++ ) {
				MetaAttribute ma = ( MetaAttribute )v.get( i );
				model.addRow(
						new Object[] { 
								ma.getName(),
								ma.getType()
						} );
			}
		}
		
		MetaNode parent = ( MetaNode )node.getParent();
		if ( parent != null ) {

			if ( parent.getParent() != null ) {
			
				model.addRow( new Object[] { "minOccurs", parent.canBeMissing( node ) ? "0" : "1" } ); 
				model.addRow( new Object[] { "maxOccurs", parent.hasMultipleOccurence( node ) ? "unbounded" : "1" } );

			}
		}

	}

	//////////////////////////////////////////////////

	class DefaultEditor implements TableCellEditor, ActionListener {

		private JComboBox secondColumn = new JComboBox(
				MetaObject.AVAILABLE_TYPES );
		
		public void actionPerformed(ActionEvent e) {
			stopCellEditing();
		}		
		
		public void addCellEditorListener(CellEditorListener l) {
			secondColumn.addActionListener( DefaultEditor.this );
		}
		public void cancelCellEditing() {
		}
		public Object getCellEditorValue() {
			return secondColumn.getSelectedItem();
		}

		private int currentRow = -1;
		
		public Component getTableCellEditorComponent(
				JTable table,
				Object value, 
				boolean isSelected, 
				int row, 
				int column ) {
			currentRow = row;
			
			String name = ( String )table.getValueAt( row, 0 );
			if ( "minOccurs".equals( name ) || 
					"maxOccurs".equals( name ) ) {
				secondColumn.setModel( new DefaultComboBoxModel( MetaObject.OCCURENCES ) );
			} else {
				secondColumn.setModel( new DefaultComboBoxModel( MetaObject.AVAILABLE_TYPES ) );
			}

			secondColumn.setSelectedItem( ( String )value );
			return secondColumn;
		}

		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		public void removeCellEditorListener(CellEditorListener l) {
			secondColumn.removeActionListener( DefaultEditor.this );
		}

		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		public boolean stopCellEditing() {			
			if ( currentRow != -1 ) {
				MetaNode node = ( MetaNode )tree.getSelectionPath().getLastPathComponent();
				int row = currentRow;

				String name = ( String )table.getValueAt( row, 0 );
				String value = ( String )secondColumn.getSelectedItem();
				
				if ( "minOccurs".equals( name ) ) {
					MetaNode parentNode = ( ( MetaNode )node.getParent() );
					if ( parentNode != null ) {
						if ( "0".equals( value ) ) {
							parentNode.setMissing( node, true );
						} else
							parentNode.setMissing( node, false );
					}
					
				} else
				if ( "maxOccurs".equals( name ) ) {
					
					MetaNode parentNode = ( ( MetaNode )node.getParent() );
					if ( parentNode != null ) {
						
						if ( "unbounded".equals( value ) ) {
							parentNode.setMultipleOccurence( node, true );
						} else
							parentNode.setMultipleOccurence( node, false );
						
					}

				} else {
					( ( MetaAttribute )node.getAttributes().get( row ) ).setType( value );
				}

				model.setValueAt( secondColumn.getSelectedItem(), currentRow, 1 );

			}
			return true;
		}
	}

	class DefaultRenderer implements TableCellRenderer {

		private JComboBox secondColumn = new JComboBox();
		
		public DefaultRenderer() {
			for ( String i : MetaObject.AVAILABLE_TYPES ) {
				secondColumn.addItem( i );
			}
			for ( String i : MetaObject.OCCURENCES ) {
				secondColumn.addItem( i );
			}			
		}
		
		public Component getTableCellRendererComponent(
				JTable table,
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row,
				int column ) {

			secondColumn.setSelectedItem( ( String )value );

			secondColumn.setBackground( isSelected ? 
					table.getSelectionBackground() : 
						table.getBackground() );
			secondColumn.setForeground( isSelected ? 
					table.getSelectionForeground() : 
						table.getForeground() );

			return secondColumn;
		}

	}
	
}
