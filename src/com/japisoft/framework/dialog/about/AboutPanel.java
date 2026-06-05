// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.dialog.about;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.Toolkit;
import com.japisoft.framework.ui.table.StringTableCellRenderer;
import com.japisoft.framework.ui.toolkit.FileManager;

import java.util.*;
import java.util.Map.Entry;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class AboutPanel extends JPanel implements ActionListener {
	JLabel lblProduct = new JLabel();
	JLabel lblProductName = new JLabel();
	JLabel lblVersion = new JLabel();
	JLabel lblProductVersion = new JLabel();
	JLabel lblCompany = new JLabel();
	JLabel lblCompanyName = new JLabel();
	Box box1;
	JLabel lblMemory = new JLabel();
	JProgressBar pbMemory = new JProgressBar();
	JButton btnGc = new JButton();
	JTabbedPane tpMain = new JTabbedPane();
	JPanel pnlProduct = new JPanel();
	JLabel lblProductImage = new JLabel();
	JLabel lblProductRegisteredVersion = new JLabel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel pnlSystem = new JPanel();
	JScrollPane spSystem = new JScrollPane();
	BorderLayout borderLayout2 = new BorderLayout();

	JTable tbSystem = new JTable() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	JButton btnExport = new JButton( "Export..." );

	public final static String PRODUCT_KEY = "lblProductName";
	public final static String VERSION_KEY = "lblProductVersion";
	public final static String BUILD_KEY = "lblBuildName";
	public final static String IMAGE_KEY = "lblProductImage";
	public final static String COMPANY_KEY = "lblCompanyName";
	public final static String REGISTERED_KEY = "lblProductRegisteredVersion";

	public AboutPanel(HashMap state) {
		if (state == null) {
			throw new RuntimeException( "The state is required" );
		}
		initUI();
		buildSystem();
		initState(state);
		
		pbMemory.setForeground( ApplicationModel.getSharedProperty( "about.memory.foreground",  new Color( Integer.parseInt( "3D4B85", 16 )  ) ) );
	}

	private void initState(HashMap state) {
		Toolkit.mapper( state, this );
	}

	private static Properties EXTERNAL_PROPERTY = null;
	
	/** Static access for specific properties like libraries version... thay must be
	 * visible by the user
	 */
	public static void addAboutProperty( String key, String value ) {
		if ( EXTERNAL_PROPERTY == null )
			EXTERNAL_PROPERTY = new Properties();
		if ( value != null )
			EXTERNAL_PROPERTY.put( key, value );
	}

	private void buildSystem() {
		DefaultTableModel model = new DefaultTableModel(new String[] {
				"Property", "Value" }, 
				0
		);
		
		model.addRow(new String[] { "EDITIX VERSION", ApplicationModel.getAppVersion() } );
		
		if ( EXTERNAL_PROPERTY != null ) {
			Enumeration enum2 = EXTERNAL_PROPERTY.keys();
			while ( enum2.hasMoreElements() ) {
				String property = ( String )enum2.nextElement();
				String value = EXTERNAL_PROPERTY.getProperty( property );
				model.addRow(
						new Object[] { property, value } );
			}
		}
		Properties prop = System.getProperties();
		Enumeration enume = prop.keys();
				
		while (enume.hasMoreElements()) {
			String property = (String) enume.nextElement();
			String value = prop.getProperty(property);
			model.addRow(new Object[] { property, value });
		}
		tbSystem.setModel(model);
		StringTableCellRenderer.fillIt( tbSystem, true, false );
		spSystem.setPreferredSize( new Dimension( 200, 200 ) );
	}

	private void initUI() {
		box1 = Box.createVerticalBox();
		this.setLayout(gridBagLayout1);
		lblProduct.setText("Product :");
		lblProductName.setText("...");
		lblVersion.setText("Version :");
		lblProductVersion.setText("...");
		lblCompany.setText("Author :");
		lblCompanyName.setText("...");
		lblMemory.setText("Memory :");
		btnGc.setIcon(Toolkit.getIconFromClasspath( "images/garbage_make_empty.png") );
		
		lblProductName.setName( PRODUCT_KEY );
		lblProductVersion.setName( VERSION_KEY );
		lblBuildName.setName( BUILD_KEY );
		lblProductImage.setName( IMAGE_KEY );
		lblCompanyName.setName( COMPANY_KEY );
		lblProductRegisteredVersion.setName( REGISTERED_KEY );

		pnlProduct.setLayout(borderLayout1);
		pnlSystem.setLayout(borderLayout2);

		lblBuild.setText( "Build :" );
		lblBuildName.setText( "..." );
		this.add(lblProduct, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4,
						10, 0, 0), 0, 0));
		this.add(lblProductName, new GridBagConstraints(2, 0, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4,
						0, 0, 15), 0, 0));
		this.add(lblVersion, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4,
						10, 0, 9), 0, 0));
		this.add(lblProductVersion, new GridBagConstraints(2, 1, 2, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(4, 0, 0, 15), 0, 0));
		this.add(tpMain, new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 2, 2, 2), 0, 0));
		this.add(lblBuild, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4,
						10, 0, 0), 0, 0));

		tpMain.add(pnlProduct, "Product");
		pnlProduct.add(lblProductImage, BorderLayout.CENTER);
		pnlProduct.add( lblProductRegisteredVersion, BorderLayout.SOUTH );

		tpMain.add(pnlSystem, "System");
		pnlSystem.add(spSystem, BorderLayout.CENTER);
		spSystem.getViewport().add(tbSystem, null);

		this.add(lblBuildName, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4,
						7, 0, 9), 0, 0));
		this.add(lblCompany, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4,
						10, 0, 0), 0, 0));
		this.add(lblCompanyName, new GridBagConstraints(2, 3, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4,
						6, 0, 10), 0, 0));
		this.add(pbMemory, new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(btnGc, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		this.add(lblMemory, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4,
						10, 0, 7), 0, 0));

		tpMain.setSelectedComponent(pnlProduct);
		pbMemory.setMinimum(0);
		pbMemory.setMaximum(100);
		
		tbSystem.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );
		
		
		pnlSystem.add( btnExport, BorderLayout.SOUTH );
	}

	private MemoryThread thread = null;

	JLabel lblBuild = new JLabel();
	JLabel lblBuildName = new JLabel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	public void addNotify() {
		super.addNotify();
		btnGc.addActionListener(this);
		thread = new MemoryThread();
		thread.start();		
		btnExport.addActionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		btnGc.removeActionListener(this);
		btnExport.removeActionListener( this );
		thread.setStop();
		thread = null;
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btnGc )
			System.gc();
		if ( e.getSource() == btnExport ) {
			File result = FileManager.getSelectedFile( true, "txt", "Text file..." );
			if ( result != null ) {
				try {
					BufferedWriter bw = new BufferedWriter( new FileWriter( result ) );
					try {
						TableModel model = tbSystem.getModel();
						bw.write( "[System properties]" );
						bw.newLine();
						for ( int i = 0; i < model.getRowCount(); i++ ) {
							String key = (String)model.getValueAt( i, 0 );
							String value = (String)model.getValueAt( i, 1 );
							bw.write( key );
							bw.write( "\t" );
							bw.write( value );
							bw.newLine();
						}
						bw.write( "[Preferences]");
						bw.newLine();
						Properties prop = Preferences.getCurrentPreferences();
						Set<Entry<Object,Object>> entries = prop.entrySet();
						for ( Entry<Object,Object> entry : entries ) {
							String key = (String)entry.getKey();
							Object tmp = entry.getValue();
							String value = tmp.toString();
							if ( tmp instanceof Font ) {
								Font f = ( Font )tmp;
								value = "Font [" + f.getFontName() + " - size:" + f.getSize() + " - style:" + f.getStyle();
							}							
							bw.write( key );
							bw.write( "\t" );
							bw.write( value );
							bw.newLine();							
						}
						
					} finally {
						bw.close();
					}					
				} catch( Exception exc ) {
					exc.printStackTrace();
				}
			}
		}
	}

	class MemoryThread extends Thread {

		boolean stop = false;

		void setStop() {
			this.stop = true;
		}

		public void run() {
			try {
				while (!stop) {
					Thread.sleep(1000);
					double maxMemory = Runtime.getRuntime().freeMemory();
					double total = Runtime.getRuntime().totalMemory();
					int ratio = 100 - ((int) ((maxMemory / total) * 100));
					pbMemory.setValue(ratio);
				}
			} catch (InterruptedException exc) {
			}
		}
	}
	
}
