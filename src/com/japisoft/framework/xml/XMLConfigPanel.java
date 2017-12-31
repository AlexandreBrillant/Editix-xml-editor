package com.japisoft.framework.xml;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.japisoft.framework.ApplicationModel;

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
public class XMLConfigPanel extends JPanel implements ActionListener,
		ListSelectionListener {

	JList listJars = new JList();
	JPanel pnlJars = new JPanel();
	TitledBorder titledBorder1;
	JButton btnAdd = new JButton();
	JButton btnRemove = new JButton();
	JButton btnSearch = new JButton( "Search a JAXP class" );
	JPanel pnlClass = new JPanel();
	TitledBorder titledBorder2;
	JTextField tfClassname = new JTextField();
	JButton btnCheck = new JButton();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	BoxLayout boxLayout21 = new BoxLayout(pnlJars, BoxLayout.X_AXIS);
	Class interf = null;
	String dataFileName = null;

	public XMLConfigPanel(Class interf, String dataFileName) {
		this.interf = interf;
		this.dataFileName = dataFileName;
		listJars.setModel(new DefaultListModel());
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		init();
		updateBtnStatus();
	}

	private void init() {
		File data = new File( ApplicationModel.getAppUserPath(), dataFileName );
		if ( data.exists() ) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(data));
				try {
					tfClassname.setText(reader.readLine());
					String line = reader.readLine();
					while (line != null) {
						((DefaultListModel) listJars.getModel())
								.addElement(line);
						line = reader.readLine();
					}
				} finally {
					reader.close();
				}
			} catch (IOException exc) {
				System.err.println(exc.getMessage());
			}
		}
	}

	public boolean save() {
		File data = new File(ApplicationModel.getAppUserPath(),
				dataFileName);
		if ( listJars.getModel().getSize() > 0 && 
				!"".equals( tfClassname.getText() ) ) {
			
			try {
				BufferedWriter writer = new BufferedWriter( new FileWriter( data ) );
				try {
					writer.write( tfClassname.getText() );
					writer.newLine();
					ListModel model = listJars.getModel();
					for ( int i = 0; i < model.getSize(); i++ ) {
						writer.write( ( String )model.getElementAt( i ) );
						writer.newLine();
					}
				} finally {
					writer.close();
				}
			} catch( Exception exc ) {
			}
			return true;
		} else {
			data.delete();
			return false;
		}
	}

	void jbInit() throws Exception {
		titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(
				new Color(153, 153, 153), 2), "Java Jars");
		titledBorder2 = new TitledBorder(BorderFactory.createLineBorder(
				new Color(153, 153, 153), 2), "Java Class");
		this.setLayout(gridBagLayout3);
		pnlJars.setBorder(titledBorder1);
		pnlJars.setLayout(boxLayout21);
		btnAdd.setText("Add");
		btnRemove.setText("Remove");
		pnlClass.setBorder(titledBorder2);
		pnlClass.setLayout(gridBagLayout2);
		btnCheck.setText("Check");
		tfClassname.setText("");
		this.add(new JScrollPane(listJars), new GridBagConstraints(0, 0, 1, 5,
				1.0, 10.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(pnlJars, new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0,
				GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		this.add(pnlClass, new GridBagConstraints(0, 7, 1, 1, 1.0, 1.0,
				GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		pnlClass.add(tfClassname, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		pnlClass.add(btnCheck, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		pnlJars.add(btnAdd, null);
		pnlJars.add(btnRemove, null);
		pnlJars.add(btnSearch, null);
	}

	public void addNotify() {
		super.addNotify();
		btnAdd.addActionListener(this);
		btnRemove.addActionListener(this);
		btnCheck.addActionListener(this);
		btnSearch.addActionListener(this);
		listJars.addListSelectionListener(this);
	}

	public void removeNotify() {
		super.removeNotify();
		btnAdd.removeActionListener(this);
		btnRemove.removeActionListener(this);
		btnCheck.removeActionListener(this);
		btnSearch.removeActionListener(this);
		listJars.removeListSelectionListener(this);
	}

	public void valueChanged(ListSelectionEvent e) {
		updateBtnStatus();
	}

	private void updateBtnStatus() {
		btnRemove.setEnabled(listJars.getSelectedIndex() != -1);
		btnSearch.setEnabled(listJars.getModel().getSize() > 0);
	}

	JFileChooser chooser = null;

	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btnAdd ) {
			if ( chooser == null ) {
				chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled( true );
			}
			if ( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				File[] files = chooser.getSelectedFiles();
				try {
					for ( int i = 0; i < files.length; i++ ) {
						((DefaultListModel)listJars.getModel()).addElement( files[ i ].toURL().toString() );
					}
					updateBtnStatus();
				} catch( MalformedURLException exc ) {
					JOptionPane.showMessageDialog( this, "Can't add this file : " +  exc.getMessage() );
				}
			}
		} else
		if ( e.getSource() == btnRemove ) {
			( ( DefaultListModel )listJars.getModel() ).removeElementAt(
					listJars.getSelectedIndex() );
			updateBtnStatus();
		} else 
		if ( e.getSource() == btnCheck ) {
			ListModel model = listJars.getModel();
			URL[] urls = new URL[ model.getSize() ];
			try {
				for ( int i = 0; i < model.getSize(); i++ )
					urls[ i ] = new URL( ( String )model.getElementAt( i ) );
				boolean checked = XMLToolkit.check( urls, tfClassname.getText() );
				if ( checked ) {
					JOptionPane.showMessageDialog( this, tfClassname.getText() + " checked" );
				} else {
					JOptionPane.showMessageDialog( this, "Can't use " + tfClassname.getText() );
				}
			} catch( MalformedURLException exc ) {
				JOptionPane.showMessageDialog( this, "Can't checked : " + exc.getMessage() ); 
			}
		} else
		if ( e.getSource() == btnSearch ) {
			try {
				ListModel model = listJars.getModel();
				URL[] urls = new URL[ model.getSize() ];
				for ( int i = 0; i < model.getSize(); i++ )
					urls[ i ] = new URL( ( String )model.getElementAt( i ) );
				URLClassLoader classLoader = new URLClassLoader( urls );
				boolean found = false;
				for ( int i = 0; i < model.getSize(); i++ ) {
					String res = check( classLoader, urls[ i ] );
					if ( res != null ) {
						tfClassname.setText( res );
						found = true;
						break;
					}
				}
				if ( !found ) {
					JOptionPane.showMessageDialog( this, "Can't find a class compatible with " + interf );
				}
			} catch( Exception exc ) {
				JOptionPane.showMessageDialog( this, "Can't find a class compatible with " + interf );
			}
		}
	}

	private String check( URLClassLoader loader, URL jars ) throws Exception {
		JarInputStream input = new JarInputStream( jars.openStream() );
		try {
			JarEntry je = input.getNextJarEntry();
			while ( je != null ) {
				String name = je.getName();
				if ( name.endsWith( ".class" ) ) {
					name = name.replace( '/', '.' );
					name = name.replace( '\\', '.' );
					name = name.substring( 0, name.length() - 6 );
					try {
						Class cl = loader.loadClass( name );
						if ( interf.isAssignableFrom( cl ) && cl != interf ) {
							return name;
						}
					} catch( Throwable exc ) {
						System.err.println( "Can't load " + name );
					}
				}
				je = input.getNextJarEntry();
			}
		} finally {
			input.close();
		}
		return null;
	}

}
