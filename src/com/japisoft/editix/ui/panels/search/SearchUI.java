package com.japisoft.editix.ui.panels.search;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.table.StringTableCellRenderer;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.NamespacePrefixCriteria;
import com.japisoft.framework.xml.parser.walker.NamespaceURICriteria;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TextCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.framework.xml.parser.walker.ValidCriteria;
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
public class SearchUI 
	extends JPanel 
		implements 
			ActionListener,
			ListSelectionListener {

	public SearchUI() {
		jbInit();	
		
		getActionMap().put( "run", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				btnApplyFromRoot.doClick();
			}
		});
		
		getInputMap( JPanel.WHEN_IN_FOCUSED_WINDOW ).put( 
				KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "run" );
		
		Icon i = com.japisoft.framework.ui.Toolkit.getIconFromClasspath("images/gear_run.png");		
		
		btnApplyFromRoot.setIcon( i );
		btnApplyFromCurrent.setIcon( i );
	}

	JLabel lblCriteria = new JLabel();

	JComboBox cbCriteria = new JComboBox(
		new String[] {
			"ELEMENT",
			"ATTRIBUTE NAME",
			"ATTRIBUTE VALUE",
			"TEXT",
			"NAMESPACE PREFIX",
			"NAMESPACE URI"
		} );

	JLabel jLabel1 = new JLabel(), jLabel2 = new JLabel();
	JTextField txtValue = new JTextField();
	JButton btnApplyFromRoot = new JButton();
	JButton btnApplyFromCurrent = new JButton();
	JScrollPane spResult = new JScrollPane();
	JTable tbResult = new ExportableTable() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	private void jbInit() {
		tbResult.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );

        jLabel1.setText("Select your search criteria");
        jLabel2.setText("<html><body>Value <b><font size='-2'>(Enter for running)</font></b></b></body></html>");
        btnApplyFromRoot.setText("Search From Root");
        btnApplyFromRoot.setToolTipText("Search from root node");
        btnApplyFromCurrent.setText("Search From Current");
        btnApplyFromCurrent.setToolTipText("search from the current node");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spResult, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cbCriteria, 0, 228, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnApplyFromRoot)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnApplyFromCurrent))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbCriteria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnApplyFromRoot)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnApplyFromCurrent)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spResult, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addContainerGap())
        );

        spResult.getViewport().add(tbResult, null);
	}

	public void addNotify() {
		super.addNotify();
		btnApplyFromCurrent.addActionListener( this );
		btnApplyFromRoot.addActionListener( this );
		tbResult.getSelectionModel().addListSelectionListener(this);	
		txtValue.addActionListener( this );
	}	
	
	public void removeNotify() {
		super.removeNotify();
		btnApplyFromCurrent.removeActionListener( this );
		btnApplyFromRoot.removeActionListener( this );
		tbResult.getSelectionModel().removeListSelectionListener(this);
		txtValue.removeActionListener( this );
	}
	
	private String lastDocumentLocation = null;
	
	// Special case when activating by the editor popup
	public void searchByParam( String param ) {
		if ( param.startsWith( "e" ) ) {
			cbCriteria.setSelectedIndex( 0 );
		} else
		if ( param.startsWith( "a" ) ) {
			cbCriteria.setSelectedIndex( 1 );
		}
		txtValue.setText( param.substring( 1 ) );
		actionPerformed( null );
	}

	public void actionPerformed( ActionEvent e ) {
		FPNode node = null;
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}
		
		lastDocumentLocation = container.getCurrentDocumentLocation();
		
		if ( e != null ) {
			if ( e.getSource() == btnApplyFromCurrent ) {
				node = container.getCurrentNode();			
			} else
				if ( e.getSource() == btnApplyFromRoot ) {
					if ( container.getTree() == null ) {
						EditixFactory.buildAndShowErrorDialog( "No available tree" );
						return;
					}
					node = ( FPNode )container.getTree().getModel().getRoot();
				} else {
					// From textfield event
					node = ( FPNode )container.getTree().getModel().getRoot();
				}
		} else {
			node = ( FPNode )container.getTree().getModel().getRoot();
		}

		if ( node == null )
			EditixFactory.buildAndShowErrorDialog( "No node" );
		else {
			unshowResult();
			TreeWalker tw = new TreeWalker( node );

			ValidCriteria vc = null;
			
			if ( cbCriteria.getSelectedIndex() == 0 ) {
				vc = new NodeNameCriteria( txtValue.getText() );
			}
			else
			if ( cbCriteria.getSelectedIndex() == 1 ) 
				vc = new AttributeCriteria( txtValue.getText() );
			else
			if ( cbCriteria.getSelectedIndex() == 2 ) 
				vc = new AttributeCriteria( AttributeCriteria.ANY_ATTRIBUTE, txtValue.getText() );
			else
			if ( cbCriteria.getSelectedIndex() == 3 ) 
				vc = new TextCriteria( txtValue.getText() );
			else
			if ( cbCriteria.getSelectedIndex() == 4 )
				vc = new NamespacePrefixCriteria( txtValue.getText() );
			else
			if ( cbCriteria.getSelectedIndex() == 5 )
				vc = new NamespaceURICriteria( txtValue.getText() );
				
			showResult( 
					tw.getNodeByCriteria( vc, true ), 
					txtValue.getText(), 
					cbCriteria.getSelectedIndex() );
		}
	}

	private void showResult( Enumeration e, String value, int type ) {
		String secondColomn = null;
		ArrayList r = new ArrayList();
		boolean added = false;
		
		for ( ; e.hasMoreElements() ; ) {
			
			added = false;
			
			FPNode result = 
				( FPNode )e.nextElement();

			if ( result.isText() )
				result = result.getFPParent();

			if ( result.isTag() ) {
				
				if ( type == 1 ) {
					secondColomn = value + " attribute";
					added = true;
					r.add( new Object[] {
							result,
							result.getAttribute( value )
					} );
				}

				if ( !added ) {
					if ( !result.isLeaf() ) {
						FPNode childNode = result.childAt( 0 );
						if ( childNode.isText() ) {
							if ( secondColomn == null )
								secondColomn = "First text";
	
							added = true;
							r.add(
									new Object[] {
											result,
											childNode.getContent()
									} );
						}
					} 
	
					if ( !added ) {
						
						if ( result.getFirstAttributeValue() != null ) {
							if ( secondColomn == null )
								secondColomn = "First attribute";
							r.add( 
									new Object[] {
											result,
											result.getFirstAttributeValue()		
									} 
							);
						} else
							r.add(
									new Object[] {
											result	
									} );
						}
	
					}
				}
		}

		DefaultTableModel model = null;

		if ( secondColomn != null )
			model = new DefaultTableModel( 0, 2 );
		else
			model = new DefaultTableModel( 0, 1 );

		for ( int i = 0; i < r.size(); i++ )
			model.addRow( 
					( Object[] )r.get( i ) 
			);

		if ( secondColomn != null ) {
			model.setColumnIdentifiers( 
					new String[] { 
							"Node (" + r.size() + " found)", 
							secondColomn } );
		} else {
			model.setColumnIdentifiers(
					new String[] {
							"Node (" + r.size() + " found)"
					} );			
		}

		tbResult.setModel( model );

		TableRowSorter<TableModel> sorter = 
			new TableRowSorter<TableModel>( model );
		
		sorter.setComparator( 0, 
			new Comparator<FPNode>() {
				public int compare(FPNode o1, FPNode o2) {
					return o1.getContent().compareTo( o2.getContent() );
				}
			} 
		);

		tbResult.setRowSorter( sorter ); 
		
		StringTableCellRenderer.fillIt( tbResult );
	}

	private void unshowResult() {
		DefaultTableModel model =
			new DefaultTableModel(
					new String[] {" No result " }, 
					0
			);
		tbResult.setModel( model );
	}

	public void valueChanged(ListSelectionEvent e) {
		int row = tbResult.getSelectedRow();

		if (row > -1) {
			
			row = tbResult.convertRowIndexToModel( row );
			
			FPNode n = (FPNode) tbResult.getModel().getValueAt(row, 0);
			
			if ( lastDocumentLocation != null ) {
				boolean ok = EditixFrame.THIS.activeXMLContainer( lastDocumentLocation );
				if ( !ok )
					return;
			}

			XMLContainer container =
				EditixFrame.THIS.getSelectedContainer();
			if (n.getStartingLine() > 0 && container != null ) {
				container.getEditor().highlightLine(n.getStartingLine());
			}
		}
	}

}
