package com.japisoft.editix.ui.xslt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.japisoft.editix.ui.xslt.profiler.ProfilerElement;
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
public class ProfilerContainer extends JPanel implements ListSelectionListener {

	private LineSelectionListener container;

	public ProfilerContainer( LineSelectionListener container ) {
		super();
		initUI();
		this.container = container;
	}

	private JTable table = null;
	private NodeRenderer nr = new NodeRenderer();

	private void initUI() {
		table = new ExportableTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			public TableCellRenderer getCellRenderer(int row, int column) {
				return nr;
			}
		};
		DefaultTableModel dtm = new
			DefaultTableModel( new String[] { "Node", "Time", "Iter" }, 0 );
		table.setModel( dtm );
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );
		updateTableColumnSize();
		setLayout( new BorderLayout() );
		add( new JScrollPane( table ) );
	}

	private void updateTableColumnSize() {
		table.getColumnModel().getColumn( 0 ).setWidth( 150 );
		table.getColumnModel().getColumn( 0 ).setPreferredWidth( 150 );
		table.getColumnModel().getColumn( 2 ).setMaxWidth( 30 );		
	}

	public void addNotify() {
		super.addNotify();
		linkListeners();
	}

	public void removeNotify() {
		super.removeNotify();
		unlinkListners();
	}

	private void linkListeners() {
		table.getSelectionModel().addListSelectionListener( this );
	}
	private void unlinkListners() {
		table.getSelectionModel().removeListSelectionListener( this );
	}

	public void updateProfilerContext( ArrayList context ) {
		unlinkListners();
		
		double totalTime = 0;
		for ( int i = 0; i < context.size(); i++ ) {
			ProfilerElement pe = ( ProfilerElement )context.get( i );
			totalTime += pe.totalTime;
		}

		DefaultTableModel dtm = new
		DefaultTableModel( new String[] { "Node", "Time", "Iter" }, 0 );
				
		// Percent
		for ( int i = 0; i < context.size(); i++ ) {
			ProfilerElement pe = ( ProfilerElement )context.get( i );
			pe.timePercent = (int)Math.round( ( pe.totalTime / totalTime ) * 100.0 );			
			dtm.addRow( new Object[] { pe, pe, pe } );
		}

		table.setModel( dtm );
		updateTableColumnSize();
		table.getSelectionModel().setSelectionInterval( 0 , 0 );

		linkListeners();
	}

	public void valueChanged(ListSelectionEvent e) {
		if ( e.getFirstIndex() > -1 ) {
			ProfilerElement pe = ( ProfilerElement )table.getModel().getValueAt(
					e.getFirstIndex(), 
					0 
			);
			container.showXSLTLine( pe.uri, pe.line );
		}
	}

	class NodeRenderer implements TableCellRenderer {
		
		private JLabel label = new JLabel();
		private JProgressBar bar = new JProgressBar();

		private NodeRenderer() {
			label.setOpaque( true );
			bar.setForeground( new Color( 200, 250, 200 ) );
			bar.setMinimum( 0 );
			bar.setMaximum( 100 );
		}

		public Component getTableCellRendererComponent(
				JTable table, 
				Object value,
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {

			if ( isSelected ) {
				label.setForeground( table.getSelectionForeground() );
				label.setBackground( table.getSelectionBackground() );
				bar.setBackground( table.getSelectionBackground() );				
			} else {
				label.setForeground( table.getForeground() );
				label.setBackground( table.getBackground() );
				bar.setBackground( table.getBackground() );				
			}

			ProfilerElement element = ( ProfilerElement )value;

			if ( column == 1 ) {
				bar.setValue( element.timePercent );
				return bar;
			}
			else
			if ( column == 0 ) {
				label.setHorizontalAlignment( SwingConstants.LEFT );
				label.setText( element.name );
			}
			else
			if ( column == 2 ) {
				label.setHorizontalAlignment( SwingConstants.CENTER );
				label.setText( "" + element.iteration );
			}
			
			return label;
			
		}
	}

}
