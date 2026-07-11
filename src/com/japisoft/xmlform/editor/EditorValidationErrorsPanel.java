// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.xmlform.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.xml.validator.ErrorValidationNode;

public class EditorValidationErrorsPanel extends JPanel implements ListSelectionListener {

	private ErrorHighlighter highlighter;
	private JTable table;

	public EditorValidationErrorsPanel( ErrorHighlighter highligher ) {
		this.highlighter = highligher;
		initUI();
		setPreferredSize( 
			new Dimension( 
				0, 
				table.getFontMetrics( 
					table.getFont() ).getHeight() * 6 ) );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		table.getSelectionModel().addListSelectionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		table.getSelectionModel().removeListSelectionListener( this );
	}

	private void initUI() {
		setLayout( new BorderLayout() );
		add( new JScrollPane( 
				table = new JTable() ) );
		table.setDefaultRenderer( 
				ErrorValidationNode.class, 
				new CustomRenderer() );
		table.getSelectionModel().setSelectionMode( 
				ListSelectionModel.SINGLE_SELECTION );
	}

	public void init( List<ErrorValidationNode> errors ) {
		table.setModel( 
			new ErrorsTableModel( errors ) );
		table.getColumnModel().getColumn( 0 ).setMaxWidth( 16 );
	}

	public void valueChanged(ListSelectionEvent e) {
		ErrorValidationNode node = 
			( ErrorValidationNode )table.getModel().getValueAt( 
					e.getFirstIndex(), 
					0 );
		highlighter.highlight( node );
	}	

	class ErrorsTableModel implements TableModel {

		private List<ErrorValidationNode> errors = null;

		ErrorsTableModel( List<ErrorValidationNode> errors ) {
			this.errors = errors;
		}

		public void addTableModelListener(TableModelListener l) {}

		public Class<?> getColumnClass(int columnIndex) {
			return ErrorValidationNode.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName( int columnIndex ) {
			if ( columnIndex == 1 ) {
				if ( errors.size() == 1 )
					return Traductor.traduce( "oneerror", "1 error" );
				else
				return errors.size() + " " + Traductor.traduce( "errors2", "errors" );
			} else
				return "";
		}

		public int getRowCount() {
			return errors.size();
		}

		public Object getValueAt( int rowIndex, int columnIndex ) {
			return errors.get( rowIndex );
		}

		public boolean isCellEditable( int rowIndex, int columnIndex ) {
			return false;
		}

		public void removeTableModelListener( TableModelListener l ) {}

		public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {}
		
	}

	class CustomRenderer extends FastLabel implements TableCellRenderer {

		private Icon bugIcon = null;

		CustomRenderer() {
			bugIcon = 
				Toolkit.getImageIcon( "images/bug_red16.png" );
		}

		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {

			if ( column == 0 ) {
				setIcon( bugIcon );
				setText( null );
			} else {
				ErrorValidationNode evn = ( ErrorValidationNode )value;
				setText( evn.getMessage() );
				setIcon( null );
			}

			if ( isSelected ) {
				setBackground( table.getSelectionBackground() );
				setForeground( table.getSelectionForeground() );
			} else {
				setBackground( table.getBackground() );
				setForeground( table.getForeground() );
			}
			
			return this;
		}
	}

}
