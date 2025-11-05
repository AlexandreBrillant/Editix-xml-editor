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

package com.japisoft.editix.action.file;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.xmlpad.XMLContainer;

/**
 * List of checkBoxs for each modified document
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class SaveAllDialog extends EditixDialog {

	public SaveAllDialog() {
		super( "Save all", "Save", "Select documents to save" );
		init();
	}

	private ArrayList list;
	public ArrayList getSelections() { return list; }

	private void init() {
		JTable t = new ExportableTable() {
			public boolean editCellAt(int row, int column)  {
				return column == 0;
			}
		};

		DefaultTableModel model = new DefaultTableModel( new
			String[] { "Saved?", "Document name", "Document path" }, 0 ) {
				public boolean isCellEditable(int row, int col)
					{ return col == 0; }
			};

		list = new ArrayList();

		for ( int i = 0; i < EditixFrame.THIS.getXMLContainerCount(); i++ ) {
			XMLContainer container = EditixFrame.THIS.getXMLContainer( i );
			if ( container == null )
				continue;
			model.addRow( new Object[] {
				new Boolean( container.getEditor().isDocumentModified() ),
				container.getDocumentInfo().getDocumentName(),
				container.getDocumentInfo().getCurrentDocumentLocation()
			} );
			if ( container.getEditor().isDocumentModified() )
				list.add( new Boolean( true ) );
			else
				list.add( new Boolean( false ) );
		}
		t.setModel( model );
		t.setSelectionBackground( t.getBackground() );
		t.setSelectionForeground( t.getForeground() );
		t.getColumnModel().getColumn( 0 ).setMaxWidth( 40 );

		CustomCellEditor editor = new CustomCellEditor();
		CustomCellRenderer renderer = new CustomCellRenderer();

		t.getColumnModel().getColumn( 0 ).setCellEditor( editor );

		t.getColumnModel().getColumn( 0 ).setCellRenderer( renderer );
		t.getColumnModel().getColumn( 1 ).setCellRenderer( renderer );
		t.getColumnModel().getColumn( 2 ).setCellRenderer( renderer );

		t.getSelectionModel().setSelectionInterval( 0, 0 );

		getContentPane().add( new JScrollPane( t ) );
		setSize( 400, 250 );
	}

	class CustomCellRenderer extends DefaultTableCellRenderer {
		private JCheckBox cb = new JCheckBox();

		public CustomCellRenderer() {
			super();
			cb.setBackground( UIManager.getColor( "label.background" ) );
			cb.setForeground( UIManager.getColor( "label.foreground" ) );
			
			cb.setHorizontalAlignment( JCheckBox.CENTER );
		}

		public Component getTableCellRendererComponent(JTable table,
			Object value,
			boolean b,
			boolean isSelected,
			int row,
			int column ) {
		
			if ( column != 0 ) {
				isSelected = false;

				JLabel lbl = ( JLabel )super.getTableCellRendererComponent(
					table, value, b, isSelected, row, column );
				if ( value != null )
					lbl.setToolTipText( value.toString() );
				return lbl;
			} else {
				cb.setSelected( ( (Boolean)list.get( row ) ).booleanValue() );
				return cb;
			}
		}
	}

	class CustomCellEditor extends DefaultCellEditor  {
		private JCheckBox cb;

		public CustomCellEditor() {
			super( new JCheckBox() );
			cb = ( JCheckBox )getComponent();
			cb.setBackground( Color.white );
			cb.setHorizontalAlignment( JCheckBox.CENTER );
		}

		private int lastRow = 0;

		public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
			if ( column == 0 ) {
				lastRow = row;
				cb.setSelected( 
					( ( Boolean )list.get( row ) ).booleanValue() );
				return cb;
			} else 

			isSelected = false;

			return super.getTableCellEditorComponent(
				table,
				value,
				isSelected,
				row,
				column);			
		}

		public boolean stopCellEditing() {
			list.set( lastRow, new Boolean( cb.isSelected() ) );
			return super.stopCellEditing();
		}
		
	}

}

