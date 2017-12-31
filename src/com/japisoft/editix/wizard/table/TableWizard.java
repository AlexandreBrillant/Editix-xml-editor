package com.japisoft.editix.wizard.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import javax.swing.ListSelectionModel;

import com.japisoft.editix.wizard.Wizard;
import com.japisoft.editix.wizard.WizardContext;
import com.japisoft.framework.dialog.AutoClosableDialog;
import com.japisoft.framework.dialog.AutoClosableListener;
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
public class TableWizard extends JPanel implements Wizard, MouseListener, MouseMotionListener, ActionListener, TableWizardModel, AutoClosableDialog {

    /**
     * Creates new form TableWizard */
    public TableWizard() {
        initComponents();
        tbSize.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        tbSize.setColumnSelectionAllowed( true );
        tbSize.setTableHeader(null);
        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        mainTp.setBorder( null );
        setBorder( null );
    }

	@Override
	public void addNotify() {
		super.addNotify();
		tbSize.addMouseListener( this );
		tbSize.addMouseMotionListener( this );
		txtWidth.addActionListener( this );
		txtHeight.addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		tbSize.removeMouseListener( this );
		tbSize.removeMouseMotionListener( this );
		txtWidth.removeActionListener( this );
		txtHeight.removeActionListener( this );
	}

	public void setDialogListener(AutoClosableListener acd) {
	}
	
	public void actionPerformed(ActionEvent e) {
		int w = getTableWidth() - 1;
		int h = getTableHeight() - 1;
		tbSize.changeSelection( h, w, false, true );
	}

	public int getTableWidth() {
		try {
			return Integer.parseInt( ( txtWidth.getText() ) );
		} catch( NumberFormatException nfe ) {
			return 1;
		}
	}

	public int getTableHeight() {
		try {
			return Integer.parseInt( ( txtHeight.getText() ) );
		} catch( NumberFormatException nfe ) {
			return 1;
		}
	}
		
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
		int row = tbSize.getSelectedRowCount();
		int col = tbSize.getSelectedColumnCount();
		txtWidth.setText( "" + ( col  ) );
		txtHeight.setText( "" + ( row ) );
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}

	private WizardContext context;
	
	public void setContext(WizardContext context) {
		this.context = context;
		txtTable.setText( context.getProperty( "table", txtTable.getText() ) );
		txtTableHeader.setText( context.getProperty( "table.header", txtTableHeader.getText() ) );
		txtTableBody.setText( context.getProperty( "table.body", txtTableBody.getText() ) );
		txtHeaderCell.setText( context.getProperty( "table.header.cell", txtHeaderCell.getText() ) );
		txtTableRow.setText( context.getProperty( "table.row", txtTableRow.getText() ) );
		txtTableCell.setText( context.getProperty( "table.cell", txtTableCell.getText() ) );
		tbSize.changeSelection( 0, 0, true, false );
	}

	public String getHeaderName(int index) {
		if ( tbHeader.getRowCount() > index )
			return ( String )tbHeader.getValueAt( index, 0 );
		return null;
	}

	public String getTable() {
		return txtTable.getText();
	}

	public String getTableBody() {
		return txtTableBody.getText();
	}

	public String getTableCell() {
		return txtTableCell.getText();
	}

	public String getTableHeader() {
		return txtTableHeader.getText();
	}

	public String getTableHeaderCell() {
		return txtHeaderCell.getText();
	}

	public String getTableRow() {
		return txtTableRow.getText();
	}

	public String getHeaderWidth(int index) {
		if ( tbHeader.getRowCount() > index )
			return ( String )tbHeader.getValueAt( index, 1 );
		return null;
	}
	
	public FPNode getResult() {
		return context.getResult( this );
	}

	/**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        mainTp = new javax.swing.JTabbedPane();
        bodyPnl = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbSize = new javax.swing.JTable();
        txtWidth = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtHeight = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTable = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTableHeader = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTableBody = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTableRow = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTableCell = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtHeaderCell = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        headerPnl = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbHeader = new javax.swing.JTable();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Table size");

        tbSize.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbSize);

        txtWidth.setText("1");

        jLabel2.setText("X");

        txtHeight.setText("1");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("table");

        txtTable.setText("table");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("table header");

        txtTableHeader.setText("thead");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("table body");

        txtTableBody.setText("tbody");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("table row");

        txtTableRow.setText("tr");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("table cell");

        txtTableCell.setText("td");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("table header cell");

        txtHeaderCell.setText("th");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout bodyPnlLayout = new javax.swing.GroupLayout(bodyPnl);
        bodyPnl.setLayout(bodyPnlLayout);
        bodyPnlLayout.setHorizontalGroup(
            bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(bodyPnlLayout.createSequentialGroup()
                        .addComponent(txtWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtHeaderCell)
                    .addComponent(txtTableBody)
                    .addComponent(txtTableRow)
                    .addComponent(txtTable, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTableHeader, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTableCell)
                    .addGroup(bodyPnlLayout.createSequentialGroup()
                        .addGroup(bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3)
                            .addComponent(jLabel8))
                        .addGap(0, 184, Short.MAX_VALUE)))
                .addContainerGap())
        );

        bodyPnlLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtHeight, txtWidth});

        bodyPnlLayout.setVerticalGroup(
            bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyPnlLayout.createSequentialGroup()
                .addGroup(bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bodyPnlLayout.createSequentialGroup()
                        .addGroup(bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(bodyPnlLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel1))
                            .addGroup(bodyPnlLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(bodyPnlLayout.createSequentialGroup()
                                .addComponent(txtTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTableHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtHeaderCell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTableBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTableRow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTableCell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(bodyPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainTp.addTab("Body", bodyPnl);

        tbHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Width"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbHeader);

        javax.swing.GroupLayout headerPnlLayout = new javax.swing.GroupLayout(headerPnl);
        headerPnl.setLayout(headerPnlLayout);
        headerPnlLayout.setHorizontalGroup(
            headerPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
        );
        headerPnlLayout.setVerticalGroup(
            headerPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
        );

        mainTp.addTab("Header", headerPnl);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTp)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTp)
        );

        mainTp.getAccessibleContext().setAccessibleName("Body");
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JPanel bodyPnl;
    private javax.swing.JPanel headerPnl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane mainTp;
    private javax.swing.JTable tbHeader;
    private javax.swing.JTable tbSize;
    private javax.swing.JTextField txtHeaderCell;
    private javax.swing.JTextField txtHeight;
    private javax.swing.JTextField txtTable;
    private javax.swing.JTextField txtTableBody;
    private javax.swing.JTextField txtTableCell;
    private javax.swing.JTextField txtTableHeader;
    private javax.swing.JTextField txtTableRow;
    private javax.swing.JTextField txtWidth;
    // End of variables declaration    	
    
    public static void main( String[] args ) {
    	JFrame f = new JFrame();
    	f.add( new TableWizard() );
    	f.pack();
    	f.setVisible( true );
    }
}
