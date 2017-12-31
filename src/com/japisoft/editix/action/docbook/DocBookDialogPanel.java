package com.japisoft.editix.action.docbook;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.text.FileTextField;
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
public class DocBookDialogPanel extends JPanel 
		implements 
			ItemListener, 
			TreeSelectionListener,
			MouseMotionListener {

	public DocBookDialogPanel() {
		initComponents();
		initModels();
		
		tfOutput.setEnabled( false );		
	}

	private ParametersModel model = null;
	
	private void initModels() {
		
		cbType.addItem( "No selection" );
		cbType.addItem( "HTML" );
		cbType.addItem( "HTML Help" );
		cbType.addItem( "XHTML" );
		cbType.addItem( "Java Help" );
		cbType.addItem( "FO" );
		cbType.addItem( "PDF" );
		cbType.addItem( "XML" );
		cbType.addItem( "PRINT" );
		cbType.addItem( "PCL" );
		cbType.addItem( "PS" );
		cbType.addItem( "TXT" );
		cbType.addItem( "SVG" );

		model = new ParametersModel();
		model.read();

		DefaultMutableTreeNode root = 
			new DefaultMutableTreeNode( "Types" );
		List lst = model.getTypes();
		for ( int i = 0; i < lst.size(); i++ ) {
			 String s = ( String )lst.get( i );
			
			root.add(
				new DefaultMutableTreeNode( s ) );
			
		}
		
		paramTree.setModel( 
				new DefaultTreeModel( root ) );
	}
	
	//@Override
	public void addNotify() {
		super.addNotify();
		if ( cbType != null ) {
			cbType.addItemListener( this );
			paramTree.addTreeSelectionListener( this );
			paramTable.addMouseMotionListener( this );
		}
	}
	
	// @Override
	public void removeNotify() {
		super.removeNotify();
		cbType.removeItemListener( this );
		paramTree.removeTreeSelectionListener( this );
		paramTable.removeMouseMotionListener( this );
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		int row = paramTable.rowAtPoint( e.getPoint() );
		if ( row > -1 ) {
			String parameter = ( String )paramTable.getValueAt( row, 0 );
			paramTable.setToolTipText( model.getHelp( parameter ) );
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		
		DefaultMutableTreeNode dmtn = 
			( DefaultMutableTreeNode )paramTree.getSelectionPath().getLastPathComponent();
		if ( dmtn == null )
			return;

		String type = 
			(String)dmtn.getUserObject();
		if ( type == null )
			return;
		
		// Save the current table state
		synchronizeTableModel();		

		// Update the table
		List lst = 
			model.getParameters( type );
		DefaultTableModel tm = new
			DefaultTableModel( new Object[] { "Parameter", "Value" }, 0 ) {
			//@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		
		for ( int i = 0; i < lst.size(); i++ ) {
			
			String parameter = ( String )lst.get( i );
			String value = model.getValue( parameter );
			tm.addRow( new Object[] { parameter, value } );
			
		}
		
		paramTable.setModel( tm );
				
	}

	private void synchronizeTableModel() {
		
		if ( paramTable.getCellEditor() != null )
			paramTable.getCellEditor().stopCellEditing();
		
		TableModel tm = paramTable.getModel();
		if ( tm.getColumnCount() != 2 ) 
			return;
		for ( int i = 0; i < tm.getRowCount(); i++ ) {
			String param = ( String )tm.getValueAt( i, 0 );
			String value = ( String )tm.getValueAt( i, 1 );
			if ( value != null && !"".equals( value ) ) {
				model.setValue( param, value );
			} else
				model.setValue( param, null );
		}
	}

	public void itemStateChanged( ItemEvent e ) {
		tfOutput.setEnabled( cbType.getSelectedIndex() > 0 );
	}
	
	protected Dimension getDefaultSize() {
		return new Dimension( 400, 250 );
	}
	
	public void init( XMLContainer container ) {
		cbType.setSelectedItem( container.getProperty( "docBook.render", "HTML" ) );
		tfOutput.setText( "" + container.getProperty( "docBook.output", "" ) );
		
		for ( int i = 5; i < 100; i++ ) {
			model.setValue( ( String )container.getProperty( "xslt.param.name." + i ), 
					( String )container.getProperty( "xslt.param.value." + i ) );
		}
		
		cbPreview.setSelected( "true".equals( container.getProperty( "docBook.application" ) ) );
	}
	
	public void store( XMLContainer container ) {
		
		synchronizeTableModel();
		
		for ( int i = 5; i < 100; i++ ) {
			container.setProperty( "xslt.param.value." + i, null );
			container.setProperty( "xslt.param.name." + i, null );
		}
		
		Set values = model.getValues();
		
		int i = 5;
		
		for ( Iterator it = values.iterator(); it.hasNext(); ) {
			Entry entry = ( Entry )it.next();		
			if ( entry.getValue() != null ) {
				container.setProperty( "xslt.param.value." + i, entry.getValue() );
				container.setProperty( "xslt.param.name." + i, entry.getKey() );
			}
			
			i++;
		}

		if ( cbType.getSelectedIndex() == 0 ) {
			container.setProperty( "docBook.render", null );
			container.setProperty( "docBook.ok", null );
			container.setProperty( "docBook.output", null );
		} else {
			container.setProperty( "docBook.render", cbType.getSelectedItem() );
			container.setProperty( "docBook.ok", "true" );
			container.setProperty( "docBook.output", tfOutput.getText() );
		}
		
		container.setProperty( "docBook.application", "" + cbPreview.isSelected() );
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor. */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        tp = new javax.swing.JTabbedPane();
        pnlOutput = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbType = new javax.swing.JComboBox();
        cbPreview = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        tfOutput = new FileTextField( ( String )null, ( String )null );
        pnlParameters = new javax.swing.JPanel();
        spParameters = new javax.swing.JSplitPane();
        spParamTree = new javax.swing.JScrollPane();
        paramTree = new javax.swing.JTree();
        spParamTable = new javax.swing.JScrollPane();
        paramTable = new ExportableTable();

        jLabel1.setText("Type");

//        cbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Output file");
        
        cbPreview.setText("Display with an external application (acrobat...)" );
        
        cbPreview.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbPreview.setMargin(new java.awt.Insets(0, 0, 0, 0));
        

        org.jdesktop.layout.GroupLayout pnlOutputLayout = new org.jdesktop.layout.GroupLayout(pnlOutput);
        pnlOutput.setLayout(pnlOutputLayout);
        pnlOutputLayout.setHorizontalGroup(
                pnlOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(pnlOutputLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(pnlOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(cbType, 0, 424, Short.MAX_VALUE)
                        .add(jLabel1)
                        .add(jLabel2)
                        .add(tfOutput, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                        .add(cbPreview))
                    .addContainerGap())
            );
            pnlOutputLayout.setVerticalGroup(
                pnlOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(pnlOutputLayout.createSequentialGroup()
                    .add(21, 21, 21)
                    .add(jLabel1)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cbType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(18, 18, 18)
                    .add(jLabel2)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(tfOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(23, 23, 23)
                    .add(cbPreview)
                    .addContainerGap(102, Short.MAX_VALUE))
            );
        tp.addTab("Output", pnlOutput);

        spParameters.setDividerLocation(170);
        spParameters.setOneTouchExpandable(true);
        spParamTree.setViewportView(paramTree);

        spParameters.setLeftComponent(spParamTree);

        spParamTable.setViewportView(paramTable);

        spParameters.setRightComponent(spParamTable);

        org.jdesktop.layout.GroupLayout pnlParametersLayout = new org.jdesktop.layout.GroupLayout(pnlParameters);
        pnlParameters.setLayout(pnlParametersLayout);
        pnlParametersLayout.setHorizontalGroup(
            pnlParametersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spParameters, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
        );
        pnlParametersLayout.setVerticalGroup(
            pnlParametersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spParameters, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
        );
        tp.addTab("Parameters", pnlParameters);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
        );
    }// </editor-fold>                        
    
    
    // Variables declaration - do not modify
    private javax.swing.JCheckBox cbPreview;    
    private javax.swing.JComboBox cbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private ExportableTable paramTable;
    private javax.swing.JTree paramTree;
    private javax.swing.JPanel pnlOutput;
    private javax.swing.JPanel pnlParameters;
    private javax.swing.JScrollPane spParamTable;
    private javax.swing.JScrollPane spParamTree;
    private javax.swing.JSplitPane spParameters;
    private FileTextField tfOutput;
    private javax.swing.JTabbedPane tp;
    // End of variables declaration             	
	
}
