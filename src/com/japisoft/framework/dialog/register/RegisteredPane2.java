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

package com.japisoft.framework.dialog.register;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;


public class RegisteredPane2 extends JPanel implements ActionListener {
	
	 public RegisteredPane2() {
		 initComponents();
	 }

	 public void addNotify() {
		 super.addNotify();
		 btnLoadFromMail.addActionListener( this );
		 btnPasteKey.addActionListener( this );
		 btnPasteUsername.addActionListener( this );
		 btnPurchase.addActionListener( this );
	 }

	 public void removeNotify() {
		 super.removeNotify();
		 btnLoadFromMail.removeActionListener( this );
		 btnPasteKey.removeActionListener( this );
		 btnPasteUsername.removeActionListener( this );
		 btnPurchase.removeActionListener( this );		 
	 }
	 
	 public String getUser() { return tfUserName.getText().trim(); }

	 public String getKey() { return tfRegisteredKey.getText().trim(); } 
	 
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btnPasteUsername ) {
			tfUserName.paste();
		} else
		if ( e.getSource() == btnPasteKey ) {
			tfRegisteredKey.paste();
		} else
		if ( e.getSource() == btnLoadFromMail ) {
			readFromMail();
		} else
		if ( e.getSource() == btnPurchase ) {
			BrowserCaller.displayURL( ApplicationModel.PURCHASING_URL );
		}
	}
	
	private void readFromMail() {
		JFileChooser fc = new JFileChooser();
		if ( fc.showDialog( ApplicationModel.MAIN_FRAME, "Select your mail" ) == JFileChooser.APPROVE_OPTION ) {
			File f = fc.getSelectedFile();
			String user = null;
			String key = null;
			
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( f ), "UTF-8" ) );
				try {
					String l = null;
					while ( ( l = br.readLine() ) != null ) {
						l = l.trim();
						if ( l.startsWith( "Username" ) || l.startsWith( "Benutzername" ) ) {
							user = l.split( ":" )[ 1 ];
						}
						if ( l.startsWith( "Key" ) || l.startsWith( "Schl�ssel" ) )
							key = br.readLine();

						if ( key != null && user != null )
							break;
					}
				} finally {
					br.close();
				}
				
			} catch( Exception exc ) {
				JOptionPane.showMessageDialog( 
					ApplicationModel.MAIN_FRAME,
					"Can't open your mail",
					"Can't open your mail : " + exc.getMessage(),
					JOptionPane.WARNING_MESSAGE);
			}
			
			tfUserName.setText( user );
			tfRegisteredKey.setText( key );
		}
		
	}
	
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfUserName = new javax.swing.JTextField();
        btnPasteUsername = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tfRegisteredKey = new javax.swing.JTextArea();
        btnPasteKey = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnLoadFromMail = new javax.swing.JButton();
        btnPurchase = new javax.swing.JButton();

        jLabel1.setText("Username");

        btnPasteUsername.setText("Paste");

        jLabel2.setText("Key");

        tfRegisteredKey.setColumns(20);
        tfRegisteredKey.setRows(5);
        jScrollPane1.setViewportView(tfRegisteredKey);

        btnPasteKey.setText("Paste");

        btnLoadFromMail.setText("Load from mail");

        btnPurchase.setText("Purchase your activating key");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnLoadFromMail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                        .addComponent(btnPurchase))
                    .addComponent(btnPasteKey)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfUserName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPasteUsername))
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPasteUsername))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPasteKey)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoadFromMail)
                    .addComponent(btnPurchase))
                .addContainerGap(25, Short.MAX_VALUE))
        );
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnLoadFromMail;
    private javax.swing.JButton btnPasteKey;
    private javax.swing.JButton btnPasteUsername;
    private javax.swing.JButton btnPurchase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea tfRegisteredKey;
    private javax.swing.JTextField tfUserName;
    // End of variables declaration
    
    
    public static void main( String[] args ) {
    	JFrame f = new JFrame();
    	f.add( new RegisteredPane2() );
    	f.pack();
    	f.setVisible( true );
    }
    
}
