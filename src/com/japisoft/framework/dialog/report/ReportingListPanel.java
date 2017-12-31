package com.japisoft.framework.dialog.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.japisoft.framework.ApplicationModel;

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
public class ReportingListPanel extends JPanel 
			implements ListSelectionListener {

	private String url = null;

	public ReportingListPanel( String url ) {
		this.url = url;
		initUI();
	}
	
	private DefaultTableModel model;
	private JTextArea zoomText;
	private JTable table;
	
	private void initUI() {
		setLayout( new BorderLayout() );

		table = new JTable() {
			public boolean isCellEditable( int row, int column ) {
				return false;
			}
		};
		
		table.setAutoCreateRowSorter( true );

		JSplitPane sp = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT );

		sp.setDividerLocation( 150 );
		
		sp.setTopComponent( new JScrollPane( table ) );
		sp.setBottomComponent( new JScrollPane( zoomText = new JTextArea() ) );
		zoomText.setLineWrap( true );
		add( sp );

		model = new DefaultTableModel(
					new String[] { 
							"T", 
							"V",
							"Report", 
							"Reply" }, 0 );
		table.setModel( model );
		table.getColumnModel().getColumn( 0 ).setMaxWidth( 20 );
		table.getColumnModel().getColumn( 1 ).setMaxWidth( 30 );
		
		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		table.getSelectionModel().setSelectionInterval( 0, 0 );
		
		table.getColumnModel().getColumn( 0 ).setCellRenderer(
				new TypeColumnRenderer() );
		
	}

	public void valueChanged( ListSelectionEvent e ) {
		StringBuffer sb = new StringBuffer();
		
		String title = ( String )model.getValueAt( table.getSelectedRow(), 2 );
		String status = ( String )model.getValueAt( table.getSelectedRow(), 3 );

		sb.append( "- Report :\n" ).append( title ).append( "\n" );
		sb.append( "- Reply :\n" ).append( status );

		zoomText.setText( sb.toString() );
	}	
	
	public void addNotify() {
		super.addNotify();
		try {
			try {
				URL url2 = new URL( url );
				InputStream input = url2.openStream();
				BufferedReader buffered = new BufferedReader(
						new InputStreamReader( input ) );
				try {
					String line = null;
					
					List<Object[]> buffer = new ArrayList<Object[]>();

					while ( ( line = buffered.readLine() ) != null ) {
						int i1 = line.indexOf( "/" );
						if ( i1 == -1 )
							continue;
						int i1b = line.indexOf( "/", i1 + 1 );
						int i2 = line.lastIndexOf( ":" );
						String version = line.substring( i1 + 1, i1b );

						int tmp = version.indexOf( " " );
						if ( tmp > -1 ) {
							version = version.substring( 0, tmp );
						}

						tmp = version.indexOf( "." );
						if ( tmp > -1 ) {
							version = version.substring( 0, tmp + 2 );
						}
						
						String type = line.substring( 0, i1 );
						String title = line.substring( i1b + 1, i2 );
						String status = line.substring( i2 + 1 );
						
						Object[] mustAdd = new Object[] {
							type,
							version,
							title,
							status								
						};
						
						for ( int i = 0; i < buffer.size(); i++ ) {
							Object[] _ = buffer.get( i );
							if ( Float.parseFloat( (String)_[ 1 ] ) <= Float.parseFloat( version ) ) {
								buffer.add( i, mustAdd );
								mustAdd = null;
								break;
							}							
						}

						if ( mustAdd != null )
							buffer.add( mustAdd );
						
					}
					
					for ( Object[] tmp : buffer ) {

						model.addRow( tmp );
						
					}
					
				} finally {
					buffered.close();
				}
			} catch ( MalformedURLException e ) {
				model.addRow( new String[] { "?",
						"Cannot access to the Product site...", "Please check your Internet Connection" });
			}
		} catch ( IOException e2 ) {
			model.addRow( new String[] { "?",
					"Cannot access to the Product site...", e2.getMessage() });
		}
		table.getSelectionModel().addListSelectionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		table.getSelectionModel().removeListSelectionListener( this );
	}
	
	class TypeColumnRenderer extends JLabel implements TableCellRenderer {
		Icon iconBug = null;
		Icon iconSug = null;
		
		public TypeColumnRenderer() {
			try {
				iconBug = new
					ImageIcon(
							ClassLoader.getSystemResource( "images/bug_red16.png" )
				) ;

				iconSug = new
				ImageIcon(
						ClassLoader.getSystemResource( "images/help216.png" )
) ;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}

		// For performance
		public void validate() {
		}
		// For performance		
		public boolean isDoubleBuffered() {
			return false;
		}

		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column) {
			if ( "BUG".equals( value ) )
				setIcon( iconBug );
			else
				setIcon( iconSug );
			return this;
		}

	}

}
