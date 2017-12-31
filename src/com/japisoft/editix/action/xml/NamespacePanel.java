package com.japisoft.editix.action.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.xml.parser.node.FPNode;

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
public class NamespacePanel extends JPanel {

	private DefaultTableModel model;
	private FPNode currentNode;
	private JTable table;
	private ArrayList namespaces = null; 

	private CustomTableRenderer renderer = new CustomTableRenderer();
	private CustomTableEditor editor = new CustomTableEditor(); 
	
	public NamespacePanel(FPNode currentNode) {
		this.currentNode = currentNode;
		initUI();
		initNamespaces();
	}

	public void removeNotify() {
		super.removeNotify();
		if ( table.getCellEditor() != null )
			table.getCellEditor().stopCellEditing();
	}	
	
	public void updateNode() {
		// Check for the default one

		currentNode.removeAllNameSpaceDeclaration();
		currentNode.setNameSpace( null, null );
		
		for ( int i = 0; i < model.getRowCount(); i++ ) {
			Boolean def = ( Boolean )model.getValueAt( i, 2 );
			String prefix = ( String )model.getValueAt( i, 0 );
			String namespace = ( String )model.getValueAt( i, 1 );

			if ( namespace == null )
				namespace = "";
						
			if ( def.booleanValue() ) {

				if ( prefix != null && !"".equals( prefix ) ) {
					// There's a prefix
					currentNode.setNameSpace( prefix, namespace );
					
					if ( namespaces.indexOf( prefix ) == -1 ) {
						currentNode.addNameSpaceDeclaration( prefix, namespace );
					}
				} else {
					
					if ( !"".equals( namespace ) ) {
						currentNode.setDefaultNamespace( namespace );
					}
					
				}

			} else {
				
				if ( prefix == null || "".equals( prefix ) ) {
					currentNode.setDefaultNamespace( null );
				}

				// Check if the definition must be added
				if ( namespaces.indexOf( namespace ) == -1 ) {
					if ( prefix != null && !"".equals( prefix ) )
						currentNode.addNameSpaceDeclaration( prefix, namespace );
				}

			}
		}
		
	}
	
	private void initNamespaces() {
		namespaces = new ArrayList();
		FPNode nodeTmp = currentNode;

		// Available namespaces
		
		while ( nodeTmp != null ) {
			Iterator<String> enume = 
				nodeTmp.getNameSpaceDeclaration();
			if ( enume != null ) {
				while ( enume.hasNext() ) {
					String prefix = ( String )enume.next();
					String namespace = nodeTmp.getNameSpaceDeclarationURI( prefix );
					if ( nodeTmp != currentNode ) {
						namespaces.add( prefix );
						namespaces.add( namespace );
					}
					editor.namespace.addItem( namespace );
				}
			}
			nodeTmp = nodeTmp.getFPParent();
		}

		nodeTmp = currentNode;
		
		// Current declaration
		
		Iterator<String> enume = 
			nodeTmp.getNameSpaceDeclaration();
		if ( enume != null ) {
			while ( enume.hasNext() ) {
				String prefix = ( String )enume.next();
				String namespace = nodeTmp.getNameSpaceDeclarationURI( prefix );
				model.addRow(
						new Object[] {
								prefix,
								namespace,
								new Boolean( prefix.equals( nodeTmp.getNameSpacePrefix() ) )
						} );
			}
		}
		
		if ( nodeTmp.getDefaultNamespace() != null ) {
			model.addRow( new Object[] {
					null,
					nodeTmp.getDefaultNamespace(),
					new Boolean( nodeTmp.getNameSpacePrefix() == null )
			} );
		}
		
		if ( nodeTmp.getNameSpacePrefix() != null && 
				!nodeTmp.isNamespaceDeclared( nodeTmp.getNameSpacePrefix() ) ) {
			int i = namespaces.indexOf( nodeTmp.getNameSpacePrefix() );
			if ( i > -1 ) {
				String namespace = ( String )namespaces.get( i + 1 );
				model.addRow( new Object[] {
						nodeTmp.getNameSpacePrefix(),
						namespace,
						Boolean.TRUE
				} );
			} else {
				model.addRow( new Object[] {
						nodeTmp.getNameSpacePrefix(),
						"",
						Boolean.TRUE
				} );
			}
		}

	}

	private void initUI() {
		setLayout(new BorderLayout());
		table = new ExportableTable() {
			public TableCellRenderer getDefaultRenderer(Class columnClass) {
				return renderer;
			}

			public TableCellEditor getDefaultEditor(Class columnClass) {
				return editor;
			}
		};
		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		table.setColumnSelectionAllowed( false );

		model = new DefaultTableModel(
				new String[] { "Prefix", "Namespace", "Current" }, 0 );
		table.setModel( model );

		table.getColumnModel().getColumn( 2 ).setPreferredWidth( 50 );
		table.getColumnModel().getColumn( 0 ).setPreferredWidth( 50 );
		
		add( new JScrollPane( table ), BorderLayout.CENTER);
		JToolBar tb = new JToolBar();
		add(tb, BorderLayout.NORTH);
		
		tb.add( new AddAction() );
		tb.add( new RemoveAction() );
	}

	class AddAction extends AbstractAction {

		AddAction() {
			putValue( Action.SHORT_DESCRIPTION, "Add a new namespace definition" );
			putValue( Action.NAME, "Add a namespace definition" );
		}

		public void actionPerformed(ActionEvent e) {
			model.addRow(
					new Object[] {
							"",
							"",
							Boolean.FALSE
					} );
		}
	}

	class RemoveAction extends AbstractAction {

		RemoveAction() {
			putValue( Action.SHORT_DESCRIPTION, "Remove a namespace definition" );
			putValue( Action.NAME, "Remove a namespace definition" );
		}
		
		public void actionPerformed(ActionEvent e) {
			int row = table.getSelectedRow();
			if ( row >= 0 ) {				
				table.getCellEditor().stopCellEditing();
				model.removeRow( row );
			}
		}
	}
	
	//////////////////////////////////////////////////////////

	class CustomTableRenderer implements TableCellRenderer {

		private JLabel prefix = new JLabel();
		private JComboBox namespace = new JComboBox();
		private JCheckBox current = new JCheckBox();
		
		CustomTableRenderer() {
			prefix.setOpaque( true );
			namespace.setEditable( true );
		}
		
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			if ( isSelected ) {
				prefix.setBackground( table.getSelectionBackground() );
				prefix.setForeground( table.getSelectionForeground() );
				
				current.setBackground( table.getSelectionBackground() );
				current.setForeground( table.getSelectionForeground() );				
			} else {
				prefix.setBackground( table.getBackground() );
				prefix.setForeground( table.getForeground() );
				
				current.setBackground( table.getBackground() );
				current.setForeground( table.getForeground() );				
			}
			
			if ( value != null)
			switch (column) {
			case 0:
				prefix.setText(value.toString());
				return prefix;
			case 1:
				namespace.setSelectedItem(value.toString());
				return namespace;
			case 2:
				current.setSelected(((Boolean) value).booleanValue());
				return current;
			}
			return null;
		}
	}

	class CustomTableEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {

		private JTextField prefix = new JTextField();
		JComboBox namespace = new JComboBox();

		private JCheckBox current = new JCheckBox();
		private int lastCol = -1;
		private int lastRow = -1;
		
		CustomTableEditor() {
			current.addActionListener( CustomTableEditor.this );
			namespace.setEditable( true );
			namespace.addActionListener( CustomTableEditor.this );
		}

		public void actionPerformed( ActionEvent e ) {
			if ( lastRow == -1 )
				return;
			if ( e.getSource() == current ) {
				if ( current.isSelected() ) {
					// Disable else
					for ( int i = 0; i < model.getRowCount(); i++ ) {
						if ( i != lastRow ) {
							Boolean b = ( Boolean )model.getValueAt( i, 2 );
							if ( b.booleanValue() )
								model.setValueAt( Boolean.FALSE, i, 2 );
						}
					}
					model.setValueAt( Boolean.TRUE, lastRow, 2 );
				} else {
					model.setValueAt( Boolean.FALSE, lastRow, 2 );
				}
			} else
			if ( e.getSource() == namespace ) {
				String selectedNamespace = ( String )namespace.getSelectedItem();
				int i = namespaces.indexOf( selectedNamespace );
				if ( i >= 1 ) {
					String prefix = ( String )namespaces.get( i - 1 );
					model.setValueAt( prefix, lastRow, 0 );
				} 
				model.setValueAt( selectedNamespace, lastRow, 1 );
			}
		}
		
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.lastCol = column;
			this.lastRow = row;
			if ( value == null )
				value = "";			
			switch (column) {
			case 0:
				prefix.setText(value.toString());
				return prefix;
			case 1:
				namespace.getEditor().setItem( value );
				return namespace;
			case 2:
				current.setSelected(((Boolean) value).booleanValue());
				return current;
			}
			return null;
		}
		
		public Object getCellEditorValue() {
			switch (lastCol) {
			case 0:
				return prefix.getText();
			case 1:
				Object obj = namespace.getEditor().getItem();
				return obj;
			case 2:
				return new Boolean(current.isSelected());
			}
			return null;
		}

	}

}
