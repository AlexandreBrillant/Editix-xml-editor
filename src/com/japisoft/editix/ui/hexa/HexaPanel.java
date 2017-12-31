package com.japisoft.editix.ui.hexa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import com.japisoft.editix.ui.EditixFactory;

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
public class HexaPanel extends JPanel implements ActionListener, ListSelectionListener {

	private JButton btnRepair = null;

	public HexaPanel( Document doc, int currentLine ) {
		initComponents();
		jScrollPane1.setViewportView( new HexaTable( doc, currentLine ) );
		jComboBox1.setModel( new DefaultComboBoxModel( new Object[] { HexaTableMode.HEX, HexaTableMode.INT, HexaTableMode.CHAR } ) );
	}

	private HexaListener hl;
	
	public void setHexaListener( HexaListener hl ) {
		this.hl = hl;
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		jComboBox1.addActionListener( this );
		( ( JTable )jScrollPane1.getViewport().getView() ).getSelectionModel().addListSelectionListener( this );
		btnRepair.addActionListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		jComboBox1.removeActionListener( this );
		( ( JTable )jScrollPane1.getViewport().getView() ).getSelectionModel().removeListSelectionListener( this );
		if ( hl != null )
			hl.dispose();
		hl = null;
		btnRepair.removeActionListener( this );
	}

	public void valueChanged(ListSelectionEvent e) {
		if ( hl != null ) {
			int row = ( ( JTable )jScrollPane1.getViewport().getView() ).getSelectedRow();
			hl.selectedRow( row );
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btnRepair ) {
			
			HexaTable ht = ( HexaTable )jScrollPane1.getViewport().getView();
			if ( ht.repair() ) {
				EditixFactory.buildAndShowInformationDialog( "Your document has been repaired" );
			} else {
				EditixFactory.buildAndShowWarningDialog( "Your document needn't to be repaired" );
			}
			
		} else {
			( ( HexaTable )jScrollPane1.getViewport().getView() ).setMode( ( HexaTableMode )jComboBox1.getSelectedItem() );
		}
	}
	
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        btnRepair = new JButton( "Repair XML" );
        
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jLabel1.setText("Mode");
        jLabel1.setName("jLabel1"); // NOI18N

        jComboBox1.setName("jComboBox1"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnRepair,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(502, 502, 502))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnRepair)
                )
                .addContainerGap())
        );
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration
	
}
