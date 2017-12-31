package com.japisoft.editix.ui.xslt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.japisoft.editix.ui.xslt.debug.DebugContext;
import com.japisoft.editix.ui.xslt.debug.DebugElement;
import com.japisoft.editix.ui.xslt.debug.DebugVariable;
import com.japisoft.xmlpad.XMLContainer;

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
public class XSLTDebugContainer extends JPanel implements ListSelectionListener {

	private LineSelectionListener container;
	
	public XSLTDebugContainer( LineSelectionListener container ) {
		super();
		initUI();
		this.container = container;
	}

	public void dispose() {
		container = null;
		// --
		updateTableForNode( callStack, null );
		updateTableForNode( nodeSet, null );
	}
	
	public void updateDebugContext( DebugContext context ) {
		updateTableForNode( callStack, context.callStack );
		updateTableForNode( nodeSet, context.nodeSet );
		updateTableForVariable( variables, context.variable );
		updateTableForVariable( parameters, context.parameter );
		if ( tp.getSelectedIndex() == 0 ) {
			unLinkListeners();

			callStack.getSelectionModel().setSelectionInterval(
					callStack.getRowCount() - 1,callStack.getRowCount() - 1);

			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						JViewport viewport = (JViewport) callStack.getParent();
						java.awt.Rectangle rect = callStack.getCellRect(callStack.getRowCount() - 1, 0, true);
						java.awt.Point pos = viewport.getViewPosition();  // Shouldn't have to do this!
						rect.translate(-pos.x, -pos.y );  // Shouldn't have to do this!
						viewport.scrollRectToVisible(rect);
					}
				}
			);			

			linkListeners();
		}
		
		JTable[] tst = new JTable[] {
				callStack,
				nodeSet,
				variables,
				parameters
		};
		
		for ( int i = 0; i < tst.length; i++ )
			tp.setEnabledAt( i, tst[ i ].getRowCount() > 0 );
		
		
	}

	JTabbedPane tp = new JTabbedPane(
			JTabbedPane.BOTTOM );

	private void initUI() {
		tp.addTab( "Call Stack", getCallStack() );
		tp.addTab( "XPath Context", getNodeSet() );
		tp.addTab( "Variables", getVariables() );
		tp.addTab( "Parameters", getParameters() );
		setLayout( new BorderLayout() );
		add( tp );
	}

	private JTable callStack;
	private JTable nodeSet;
	private JTable variables;
	private JTable parameters;

	public void addNotify() {
		super.addNotify();
		linkListeners();
	}

	private void linkListeners() {
		callStack.getSelectionModel().addListSelectionListener( this );
		nodeSet.getSelectionModel().addListSelectionListener( this );
		variables.getSelectionModel().addListSelectionListener( this );		
		parameters.getSelectionModel().addListSelectionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		unLinkListeners();
	}

	private void unLinkListeners() {
		callStack.getSelectionModel().removeListSelectionListener( this );
		nodeSet.getSelectionModel().removeListSelectionListener( this );
		variables.getSelectionModel().removeListSelectionListener( this );
		parameters.getSelectionModel().removeListSelectionListener( this );
	}

	TableCellRenderer elementRenderer;

	private JTable getCommonTable() {
		JTable table = new JTable() {
			
			public boolean isCellEditable(int row,
                    int column) {
				return false;
			}
			
			public boolean isEditing() {
				return false;
			}
			
			public TableCellRenderer getCellRenderer(
					int row,
                    int column ) {
				return elementRenderer;
			}
		};
		
		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		return table;
	}
	
	private JTable prepareElementTable() {
		if ( elementRenderer == null )
			elementRenderer = new ElementRenderer();
		
		JTable table = getCommonTable();		
		updateTableForNode( table, null );
		
		return table;
	}
	
	private JScrollPane getCallStack() {
		callStack = prepareElementTable();
		return new JScrollPane( callStack );
	}
	
	private JScrollPane getNodeSet() {
		nodeSet = prepareElementTable();
		return new JScrollPane( nodeSet );
	}

	private JScrollPane getVariables() {
		JTable table = new JTable() {
			public boolean isCellEditable(int row,
                    int column) {
				return false;
			}
		};
		table.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		updateTableForVariable( variables = table, null );
		return new JScrollPane( table );
	}

	private JScrollPane getParameters() {
		JTable table = new JTable() {
			public boolean isCellEditable(int row,
                    int column) {
				return false;
			}
		};
		table.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		updateTableForVariable( parameters = table, null );
		return new JScrollPane( table );
	}

	private void updateTableForVariable( JTable table, List<DebugVariable> list ) {
		DefaultTableModel model = new DefaultTableModel(
				new String[] { "Name", "Type", "Value" }, 
				0 );
		if ( list != null ) {
			for ( int i = 0; i < list.size(); i++ ) {
				DebugVariable dv = ( DebugVariable )list.get( i );
				
				Vector v = new Vector();
				v.add( dv.name );
				v.add( dv.type );

				if ( dv.value == null )
					v.add( "[NO VALUE]" );
				else
					v.add( dv.value );
				v.add( new Integer( dv.line ) );

				model.getDataVector().addElement( v );
			}
		}

		table.setModel( model );
	}

	private void updateTableForNode( JTable table, ArrayList list ) {
		DefaultTableModel model = new DefaultTableModel(
				new String[] { "Nodes" }, 
				0 );
		if ( list != null ) {	
			if ( list.size() > 0 ) {
				
				DebugElement currentOne = null;
				
				for ( int i = 0; i < list.size(); i++ ) {
					model.addRow( 
							new Object[] { list.get( i ) } 
					);
					if ( list.get( i ) instanceof DebugElement ) {
						if ( !( ( DebugElement )list.get( i ) ).resultElement ) {
							currentOne = ( DebugElement )list.get( i );
						}
					}
				}

				// Display automatically the current node
				if ( table == nodeSet ) {
					if ( currentOne != null ) {
						container.showSourceLine( 
							currentOne.line 
						);
					}
				}

			}
		}
		table.setModel( 
			model 
		);
	}

	public void valueChanged( ListSelectionEvent e ) {
		Object selectionModel = e.getSource();
		JTable table = null;
		if ( callStack.getSelectionModel() == selectionModel ) {
			table = callStack;
		} else
		if ( nodeSet.getSelectionModel() == selectionModel ) {
			table = nodeSet;
		} else
		if ( variables.getSelectionModel() == selectionModel ) {
			table = variables;
		} else
			table = parameters;

		if ( table == null ) {
			System.err.println( "No table found ???" );
			return;
		}
		TableModel model = table.getModel();
		int index = table.getSelectedRow();
		if ( index == -1 )
			return;

		if ( ( table == variables ) || ( table == parameters ) ) {
			int line = ( ( Integer )model.getValueAt( index, 3 ) ).intValue();
			if ( line > 0 ) {
				container.showXSLTLine( null, line );
				return;
			}
		}

		if ( model.getValueAt( index, 0 ) instanceof DebugElement ) {
			DebugElement element = ( DebugElement )model.getValueAt( index, 0 );
			XMLContainer _container2 = null;
	
			if ( table == callStack ) {
				container.showXSLTLine( element.uri, element.line );
			} else
			if ( table == nodeSet ) {
				container.showSourceLine( element.line );
			}
		}
	}

	class ElementRenderer implements TableCellRenderer {
		
		private JLabel labelResult = new JLabel();
		
		private ImageIcon icon1 = new ImageIcon( 
				ClassLoader.getSystemResource( "images/media_stop.png" ) );
		
		private ImageIcon icon2 = new ImageIcon(
				ClassLoader.getSystemResource( "images/media_stop_red.png" ) );
		
		public ElementRenderer() {
			labelResult.setOpaque( true );
		}
		
		public Component getTableCellRendererComponent(
				JTable table,
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row,
				int column ) {

			if ( isSelected ) {
				labelResult.setForeground( table.getSelectionForeground() );
				labelResult.setBackground( table.getSelectionBackground() );
			} else {
				labelResult.setForeground( table.getForeground() );
				labelResult.setBackground( table.getBackground() );
			}

			DebugElement element = ( DebugElement )value;
			if ( element.resultElement )
				labelResult.setIcon( icon1 );
			else
				labelResult.setIcon( icon2 );
			
			labelResult.setText( element.toString() ); 
			
			return labelResult;
		}
	}
	
}
