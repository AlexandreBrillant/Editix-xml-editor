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

package com.japisoft.editix.db.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.japisoft.editix.db.Driver;
import com.japisoft.editix.db.DriverDbManager;
import com.japisoft.editix.db.xmldb.XmlDbDriver;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.FileManager;

public class DbDriverConfigPanel extends JPanel 
		implements 
			ActionListener, 
				ListSelectionListener {

    /** Creates new form DriverDbPanel */
    public DbDriverConfigPanel() {
        initComponents();
        resetListDrivers();
    }

    private void resetListDrivers() {
    	
    	DefaultListModel model = new DefaultListModel();
    	
    	for ( int i = 0; i < DriverDbManager.getDriverCount(); i++ ) {
    		model.addElement( DriverDbManager.getDriver( i ) );
    	}
    	
    	listDrivers.setModel( model );
    }

    public void addNotify() {
    	super.addNotify();
    	addDriver.addActionListener( this );
    	removeDriver.addActionListener( this );
    	addJar.addActionListener( this );
    	removeJar.addActionListener( this );
    	listDrivers.getSelectionModel().addListSelectionListener( this );
    }

    public void removeNotify() {
    	super.removeNotify();
    	addDriver.removeActionListener( this );
    	removeDriver.removeActionListener( this );
    	addJar.removeActionListener( this );
    	removeJar.removeActionListener( this );
    	listDrivers.getSelectionModel().removeListSelectionListener( this );
    }

    public void valueChanged(ListSelectionEvent e) {
    	Driver d = ( Driver )listDrivers.getSelectedValue();
    	resetDriver( d );
    }    

    private Driver currentDriver = null;
    
    private void resetDriver( Driver d ) {
    	
    	if ( currentDriver != null )
    		synchroDriver( currentDriver );
    	
    	removeDriver.setEnabled( !d.system );
    	txtDefaultUrl.setText( d.url );
    	txtDriverClass.setText( d.dbxmlClass );

    	DefaultListModel m = new DefaultListModel();
    	String jars = d.jars;
    	if ( jars != null ) {
	    	String[] res = jars.split( ";" );
	
	    	if ( res != null ) {
	    		for ( int i = 0; i < res.length; i++ ) {
	    			m.addElement( res[ i ] );
	    		}
	    	}
    	}
    	listJars.setModel( m );
    	this.currentDriver = d;
    }
    
    public void saveConfig()  {
    	if ( currentDriver != null )
    		synchroDriver( currentDriver );
    	DriverDbManager.save();
    }

    private void synchroDriver( Driver d ) {
    	
    	d.url = txtDefaultUrl.getText();
    	d.dbxmlClass = txtDriverClass.getText();
    	ListModel m = listJars.getModel();
    	String r = null;
    	for ( int i = 0; i < m.getSize(); i++ ) {
    		
    		if ( r == null )
    			r = ( String )m.getElementAt( i );
    		else
    			r = r + ";" + ( String )m.getElementAt( i );
    		
    	}
    	
    	d.jars = r;
    	
    }

    public void actionPerformed(ActionEvent e) {
    	if ( e.getSource() == addDriver ) {

    		String name = EditixFactory.buildAndShowInputDialog( 
    				"Driver name" 
    		);

    		if ( name != null ) {

    			Driver d = new XmlDbDriver( name );
    			DriverDbManager.addDriver( d );
    			resetListDrivers();
    			
    			ListModel model = listDrivers.getModel();
    			
    			listDrivers.getSelectionModel().setSelectionInterval( 
    					model.getSize() - 1,
    					model.getSize() - 1 );

    		}

    	} else
    	if ( e.getSource() == removeDriver ) {

    		DriverDbManager.removeDriver( 
    				( Driver )listDrivers.getSelectedValue() );
    		resetListDrivers();

    	} else
    	if ( e.getSource() == addJar ) {
    		
    		File jar = FileManager.getSelectedFile( true, "jar", "Jar file" );
    		if ( jar != null ) {
    			
    			( ( DefaultListModel )listJars.getModel() ).addElement( jar.toString() );

    		}

    	} else
    	if ( e.getSource() == removeJar ) {
    		
    		if ( listJars.getSelectedValue() != null ) {
    			
    			( ( DefaultListModel )listJars.getModel() ).removeElement( 
    					listJars.getSelectedValue() );

    		} else
    			
    			EditixFactory.buildAndShowWarningDialog( "No selected value" );

    	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor. */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        txtDriverClass = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDefaultUrl = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        addJar = new javax.swing.JButton();
        removeJar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        listJars = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listDrivers = new javax.swing.JList();
        addDriver = new javax.swing.JButton();
        removeDriver = new javax.swing.JButton();

        jLabel1.setText("XmlDb Driver class");

        txtDriverClass.setToolTipText("The XMLDb driver java class");

        jLabel2.setText("Default URL");

        txtDefaultUrl.setToolTipText("The default URL when activating a connection");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("External Jars"));
        addJar.setText("Add...");
        addJar.setToolTipText("Add a library as a jar file");

        removeJar.setText("Remove");
        removeJar.setToolTipText("Remove a library");

        jScrollPane2.setViewportView(listJars);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(addJar)
                    .add(removeJar))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(addJar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeJar)
                .add(59, 59, 59))
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Drivers"));
        jScrollPane1.setViewportView(listDrivers);

        addDriver.setText("Add...");
        addDriver.setToolTipText("Add a new xmldb Driver");

        removeDriver.setText("Remove");
        removeDriver.setToolTipText("Remove the selected driver");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(addDriver)
                    .add(removeDriver)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(addDriver)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeDriver)
                .add(77, 77, 77))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtDriverClass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtDefaultUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtDriverClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtDefaultUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>                        
    
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton addDriver;
    private javax.swing.JButton addJar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList listDrivers;
    private javax.swing.JList listJars;
    private javax.swing.JButton removeDriver;
    private javax.swing.JButton removeJar;
    private javax.swing.JTextField txtDefaultUrl;
    private javax.swing.JTextField txtDriverClass;
    // End of variables declaration                   

}
