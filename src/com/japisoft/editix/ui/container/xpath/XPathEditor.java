package com.japisoft.editix.ui.container.xpath;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.container.xpath.XPathEditorModel.XPathColumn;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.table.FeatureTable;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.XPathToolkit;
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
public class XPathEditor extends JPanel implements ActionListener, PopupMenuListener {

	public XPathEditor() {
		initComponents();
		btAddColumn.setIcon( 
			new ImageIcon(
				getClass().getResource( "element_add.png" ) 
			) 
		);
		btRemoveColumn.setIcon( 
			new ImageIcon( 
				getClass().getResource( "element_delete.png" ) 
			) 
		);
		btHelper.setIcon(
			new ImageIcon(
				getClass().getResource( "element_find.png" ) ) );
		
		btHelper.setText( null );
		btAddColumn.setText( null );
		btRemoveColumn.setText( null );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btAddColumn.addActionListener( this );
		btRemoveColumn.addActionListener( this );
		btHelp.addActionListener( this );
		btHelper.addActionListener( this );
		txtXPath.addPopupMenuListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		btAddColumn.removeActionListener( this );
		btRemoveColumn.removeActionListener( this );
		btHelp.removeActionListener( this );
		btHelper.removeActionListener( this );
		txtXPath.removePopupMenuListener( this );
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// Fill the combo box model
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container != null ) {
			List<String> tag = container.getXMLDocument().getCollectionOfElements();
			DefaultComboBoxModel dlm = new DefaultComboBoxModel();
			for ( String t : tag )
				dlm.addElement( "//" + t );
			txtXPath.setModel( dlm );
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btHelper ) {
			String xpathRoot = ( String )txtXPath.getSelectedItem();
			if ( xpathRoot == null ) {
				EditixFactory.buildAndShowWarningDialog( "Please choose a root element" );
			} else {
				String name = XPathToolkit.getXPathElementName( xpathRoot );
				XMLContainer container = EditixFrame.THIS.getSelectedContainer();
				if ( container != null ) {
					List<String> l = container.getXMLDocument().getCollectionOfAttributes( name );
					List<String> r = new ArrayList<String>();
					for ( int i = 0; i < l.size(); i++ ) {
						r.add( "@" + l.get( i ) );
					}
					l = container.getXMLDocument().getCollectionOfChildren( name );
					for ( int i = 0; i < l.size(); i++ ) {
						r.add( l.get( i ) + "/text()" );
					}
					if ( r.size() == 0 ) {
						EditixFactory.buildAndShowWarningDialog( "Can't find content" );
					} else {
						FeatureTable ft = null;
						JScrollPane sp = new JScrollPane( ft = new FeatureTable( r, "Editable part" ) );
						sp.setPreferredSize( new Dimension( 300, 300 ) );
						if ( DialogManager.showDialog( 
							EditixFrame.THIS, "Proposal content", "Proposal content", "Select parts you wish to edit", 
							null, 
							sp ) == DialogManager.OK_ID ) {
							List<String> w = ft.getSelectedFeatures();
							if ( w.size() > 0 ) {
								DefaultTableModel dtm = createTableModel();
								for ( String wi : w ) {
									if ( wi.startsWith( "@" ) ) {
										dtm.addRow( new String[] { "Attribute " + wi.substring( 1 ), wi } );
									} else {
										dtm.addRow( new String[] { "Child " + wi, wi } );
									}
								}
								tbColumns.setModel( dtm );
							}
						}
					}
				}

			}
		} else
		if ( e.getSource() == btHelp ) {
			BrowserCaller.displayURL( "http://www.w3.org/TR/xpath" );
		} else
		if ( e.getSource() == btRemoveColumn ) {
			if ( tbColumns.getSelectedRow() > -1 )
				( ( DefaultTableModel )tbColumns.getModel() ).removeRow( 
						tbColumns.getSelectedRow() 
				);
			else
				EditixFactory.buildAndShowWarningDialog( "No selected row" );
		} else 
		if ( e.getSource() == btAddColumn ) {
			( ( DefaultTableModel )tbColumns.getModel() ).addRow( 
				new Object[] { 
					"name", "text()" 
				}
			);			
		}
	}

	private DefaultTableModel createTableModel() {
		return new DefaultTableModel(
				new String[] { 
					"column name", 
					"column value (xpath)" 
				}, 
				0
			);
	}
	
	public void setEditorModel( XPathEditorModel m ) {
		txtXPath.setSelectedItem( m.getXPath() );
		DefaultTableModel model = createTableModel();
		tbColumns.setModel( model );
		for ( int i = 0; i < m.getColumnCount();i++ ) {
			XPathColumn xpc = m.getColumn( i );
			model.addRow( new Object[] {
					xpc.getName(),
					xpc.getXpath()
				} 
			);
		}
	}

	public XPathEditorModel getEditorModel() {
		XPathEditorModel m = new XPathEditorModel();
		m.setXPath( ( String )txtXPath.getSelectedItem() );
		TableModel tm = tbColumns.getModel();
		for ( int i = 0; i < tm.getRowCount(); i++ ) {
			String name = ( String )tm.getValueAt( i, 0 );
			String xpath = ( String )tm.getValueAt( i, 1 );
			if ( name == null || "".equals( name ) ) {
				name = xpath;
			}
			if ( name != null && !"".equals( name ) ) {
				m.addColumn(
					name,
					xpath
				);
			}
		}
		return m;
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        lblColumns = new javax.swing.JLabel();
        lblXPath = new javax.swing.JLabel();
        txtXPath = new javax.swing.JComboBox();
        
        txtXPath.setEditable( true );
        
        btHelp = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        spColumns = new javax.swing.JScrollPane();
        tbColumns = new ExportableTable();
        btRemoveColumn = new javax.swing.JButton();
        btAddColumn = new javax.swing.JButton();

        lblColumns.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblColumns.setText("Columns");

        lblXPath.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblXPath.setText("XPath Query");

        btHelp.setText("?");

        tbColumns.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            		new String[] { "content", "text()" }
            },
            new String [] {
            		"column name", "column value (xpath)"
            }
        ));
        spColumns.setViewportView(tbColumns);

        btRemoveColumn.setText("-");

        btAddColumn.setText("+");
        btHelper = new javax.swing.JButton();
        
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(spColumns, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(lblXPath)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(txtXPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                        .add(8, 8, 8)
                        .add(btHelp)
                        .addContainerGap())
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(lblColumns)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 117, Short.MAX_VALUE)
                        .add(btHelper)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btAddColumn)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btRemoveColumn)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblXPath)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtXPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btHelp))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblColumns)
                    .add(btRemoveColumn)
                    .add(btAddColumn)
                    .add(btHelper))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spColumns, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JButton btAddColumn;
    private javax.swing.JButton btHelp;
    private javax.swing.JButton btHelper;
    private javax.swing.JButton btRemoveColumn;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblColumns;
    private javax.swing.JLabel lblXPath;
    private javax.swing.JScrollPane spColumns;
    private javax.swing.JTable tbColumns;
    private javax.swing.JComboBox txtXPath;
    // End of variables declaration

}
