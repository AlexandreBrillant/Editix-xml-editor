package com.japisoft.xflows.task.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
class RegisteredDialog extends XFlowsDialog {

	
	public RegisteredDialog() {
		super( "Register", "Register", "You can order at http://www.xflows.com and receive instantly by mail your registered key" );
		jbInit();
	}

	JLabel lblUserName = new JLabel();
	JTextField tfUserName = new JTextField();
	JLabel lblKey = new JLabel();
	JTextField tfRegisteredKey = new JTextField();
	JTextArea jTextArea1 = new JTextArea();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	private void jbInit() {
		lblUserName.setText("Registered name");
		
		JPanel panel = new JPanel();
		
		panel.setLayout(gridBagLayout1);
		tfUserName.setText("");
		lblKey.setText("Registered key");
		tfRegisteredKey.setText("");
		panel.add(
			lblUserName,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(6, 7, 0, 268),
				10,
				0));
		panel.add(
			tfUserName,
			new GridBagConstraints(
				0,
				1,
				1,
				1,
				1.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 7, 0, 11),
				329,
				0));
		panel.add(
			lblKey,
			new GridBagConstraints(
				0,
				2,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(9, 7, 0, 225),
				27,
				0));
		panel.add(
			tfRegisteredKey,
			new GridBagConstraints(
				0,
				3,
				1,
				1,
				1.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(7, 7, 0, 11),
				329,
				0));
		panel.add(
			jTextArea1,
			new GridBagConstraints(
				0,
				4,
				1,
				1,
				1.0,
				1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(18, 7, 8, 11),
				91,
				48));
		jTextArea1.setEditable(false);
		jTextArea1.setText(
			"For purchasing xflows,\n\nhttp://www.xflows.com/buy.html");
		
		setUI( panel );
	}
	
	String getUser() { return tfUserName.getText(); }

	String getKey() { return tfRegisteredKey.getText(); } 
}
