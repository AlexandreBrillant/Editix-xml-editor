package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.japisoft.framework.preferences.Preferences;

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
public class InformationDialog extends JDialog implements ActionListener {
	
	JButton btn;
	JCheckBox cb;
	String systemKey;

	public InformationDialog( JLabel content, String systemKey ) {
		super( EditixFrame.THIS );
		this.systemKey = systemKey;
		setModal( true );
		setDefaultLookAndFeelDecorated( false );
		setTitle( "Information" );
		getContentPane().add( content );
		
		JPanel panel2 = new JPanel();
		panel2.setLayout( new BorderLayout() );
		cb = new JCheckBox( "Show this dialog the next time", true );
		btn = new JButton( "OK" );
		panel2.add( BorderLayout.WEST, cb );
		panel2.add( BorderLayout.EAST, btn );
		
		getContentPane().add( BorderLayout.SOUTH, panel2 );
		
		pack();
		setSize( getWidth() + 20, getHeight() + 20 );
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( 
				( dim.width - getWidth() ) / 2, 
				( dim.height - getHeight() ) / 2 );
	}

	public void setVisible( boolean state ) {
		super.setVisible( state );
	}
	
	public void addNotify() {
		super.addNotify();
		btn.addActionListener( this );
		cb.addActionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		btn.removeActionListener( this );
		cb.removeActionListener( this );
	}

	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == btn )
			setVisible( false );
		else {
			Preferences.setRawPreference( "system", systemKey, new Boolean( cb.isSelected() ) );
			// Preferences.setRawPreference( "system", "taskdialog" + message, new Boolean( cb.isSelected() ) );
		}
	}
}
