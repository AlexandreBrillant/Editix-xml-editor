package com.japisoft.editix.ui.panels.info;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JPanel;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.Encoding;
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
public class InfoUI extends JPanel implements ActionListener {

	InfoUI() {
		initComponents();
	}

	public void addNotify() {
		super.addNotify();
		cbEncoding.addActionListener( this );
		cbFileType.addActionListener( this );
	}	

	public void removeNotify() {
		super.removeNotify();
		cbEncoding.removeActionListener( this );
		cbFileType.removeActionListener( this );
	}

	void updateForXMLContainer( XMLContainer container ) {
		if ( container != null ) {
			lblFilePath.setText(
					container.getCurrentDocumentLocation() );			
			lblFilePath.setToolTipText(
					lblFilePath.getText() );
			jLabel1.setText( container.getDocumentInfo().getDocumentName() );
			if ( container.getCurrentDocumentLocation() != null ) {
				File f = new File( container.getCurrentDocumentLocation() );
				lblSize.setText( f.length() + " bytes" );
			} else
				lblSize.setText( "0" );
			
			String encoding = null;
			if ( container.getDocumentInfo().getEncoding() != null )
				encoding = container.getDocumentInfo().getEncoding();
			else
				encoding = ( String )container.getProperty( "encoding", "DEFAULT" );

			cbEncoding.setSelectedItem( encoding );
			String type = container.getDocumentInfo().getType();
			cbFileType.setSelectedItem( type );
		} else {
			lblFilePath.setText( null );
			lblFilePath.setToolTipText( null );
			lblSize.setText( null );
			cbEncoding.setSelectedItem( "DEFAULT" );
			cbFileType.setSelectedItem( "XML" );
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		if ( arg0.getSource() == cbEncoding ) {
			String encoding = ( String )cbEncoding.getSelectedItem();
			if ( "DEFAULT".equals( encoding ) )
				encoding = null;
			container.setProperty( "encoding", encoding );
			ProjectManager.updateFileEncoding( container.getCurrentDocumentLocation(), encoding );
		} else
		if ( arg0.getSource() == cbFileType ) {
			String type = ( String )cbFileType.getSelectedItem();
			EditixFrame.THIS.updateMenuActionForGroup( 
					container.getDocumentInfo().getType(),
					null,
					false );
			EditixFrame.THIS.updateToolBarAndMenuForType( type, null );
			com.japisoft.xmlpad.action.ActionModel.resetActionState( container );
			container.getDocumentInfo().setType( type );
			ProjectManager.updateFileType( container.getCurrentDocumentLocation(), type );
		}
	}

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        lblFilePath = new javax.swing.JTextArea();
        
        lblFilePath.setLineWrap( true );
        lblFilePath.setOpaque( false );
        lblFilePath.setEditable( false );
        lblFilePath.setPreferredSize( new Dimension( 100, 50 ) );
        
        jLabel3 = new javax.swing.JLabel();
        lblSize = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbEncoding = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cbFileType = new javax.swing.JComboBox();
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel1.setForeground( new Color( 50, 150, 50 ) );
        jLabel1.setText("Info");
        jLabel2.setText("Path");
        jLabel2.setFont(new java.awt.Font("Arial", 1, 12));
        lblFilePath.setText("...");
        jLabel3.setText("Size");
        jLabel3.setFont(new java.awt.Font("Arial", 1, 12));
        lblSize.setText("0");
        jLabel4.setText("Encoding");
        jLabel4.setFont(new java.awt.Font("Arial", 1, 12));

        cbEncoding.setModel(new javax.swing.DefaultComboBoxModel(Encoding.XML_ENCODINGS));
        jLabel5.setText("File type");
        jLabel5.setFont(new java.awt.Font("Arial", 1, 12));
        cbFileType.setModel(new javax.swing.DefaultComboBoxModel(DocumentModel.DOCUMENT_TYPE));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .add(jLabel1)
                        .add(jLabel3)
                        .add(lblSize)
                        .add(jLabel4)
                        .add(jLabel5)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbFileType, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbEncoding, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jLabel2)
                        .add(lblFilePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jLabel1)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel3)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(lblSize)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel4)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cbEncoding, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel5)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cbFileType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel2)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(lblFilePath)
                    .addContainerGap(58, Short.MAX_VALUE))
            );
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JComboBox cbEncoding;
    private javax.swing.JComboBox cbFileType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea lblFilePath;
    private javax.swing.JLabel lblSize;
    // End of variables declaration                   

}
