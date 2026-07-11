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

package com.japisoft.xflows.task;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.toolkit.Logger;
import com.japisoft.framework.toolkit.LoggerListener;
import com.japisoft.xflows.log.FileLogger;
import com.japisoft.xflows.task.ui.XFlowsFactory;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class TaskLogTable extends JTable 
	implements LoggerListener, ListSelectionListener {

	static final Integer INFO = new Integer( 0 );
	static final Integer WARNING = new Integer( 1 );
	static final Integer ERROR = new Integer( 2 );

	public TaskLogTable() {
		super();
		resetModel();
	}

	private void resetModel() {
		setModel( new DefaultTableModel(
				new Object[] { "T", "Message" }, 0 ) );
		getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );
		getColumnModel().getColumn( 0 ).setMaxWidth( 20 );
	}
	
	public void addNotify() {
		super.addNotify();
		Logger.addLoggerListener( this );
		getSelectionModel().addListSelectionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		Logger.removeLoggerListener( this );
		getSelectionModel().removeListSelectionListener( this );
	}

	public void valueChanged(ListSelectionEvent e) {
		int row = getSelectedRow();
		if  ( row != -1 ) {
			setToolTipText(
					(String)getModel().getValueAt( row, 1 ) );
		}
		repaint();
	}	

    public boolean isCellEditable(int row, int column) {
    	return false;
    }	

    public void clean() {
    	resetModel();
    }
    
    // LOGGER
    
    private void updateLogsTitle() {
    }
    
	public void addInfo( String message ) {		
		((DefaultTableModel)getModel()).addRow(
				new Object[] {
						INFO,
						message
				} );
		updateLogsTitle();
	}	

	public void addWarning( String message ) {
		((DefaultTableModel)getModel()).addRow(
				new Object[] {
						WARNING,
						message
				} );
		updateLogsTitle();
	}

	public void addError( String message ) {
		((DefaultTableModel)getModel()).addRow(
				new Object[] {
						ERROR,
						message
				} );
		updateLogsTitle();
	}

	TableRenderer renderer;

	public TableCellRenderer getDefaultRenderer( Class columnClass ) {
		if ( renderer != null )
			return renderer;
		renderer = new TableRenderer();
		return renderer;
	}

	/////////////////////////////////////

	static ImageIcon info = XFlowsFactory.getImageIcon( "images/bug_green.png" );
	static ImageIcon warning = XFlowsFactory.getImageIcon( "images/bug_yellow.png" );
	static ImageIcon error = XFlowsFactory.getImageIcon( "images/bug_red.png" );

	class TableRenderer implements TableCellRenderer {
		
		Color first = Preferences.getPreference( "scenario", "logFirstColor", new Color( 220, 220, 240 ) );
		Color second = Preferences.getPreference( "scenario", "logSecondColor", new Color( 255, 255, 255 ) );
		JLabel lbl = new JLabel();

		public TableRenderer() {
			lbl.setOpaque( true );
		}

		public Component getTableCellRendererComponent(
				JTable table, 
				Object value,
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {

			if ( row % 2 == 0 ) {
				lbl.setForeground( Color.black );
				lbl.setBackground( first );
			} else { 
				lbl.setForeground( Color.black );
				lbl.setBackground( second );
			}
			
			if ( isSelected ) {
				Color _tmp = lbl.getForeground();
				lbl.setForeground ( lbl.getBackground() );
				lbl.setBackground( _tmp );
			}

			if ( column == 1 ) {
			
				lbl.setIcon( null );
				if ( value != null ) 
					lbl.setText( value.toString() );
				else
					lbl.setText( null );
			
			} else {

				if ( value == INFO ) {
					lbl.setIcon( info );
				}
				else
				if ( value == WARNING ) {
					lbl.setIcon( warning );
				}
				else
				if ( value == ERROR ) {
					lbl.setIcon( error );
				}

				lbl.setText( null );
				
			}

			return lbl;
		}
	}

}
