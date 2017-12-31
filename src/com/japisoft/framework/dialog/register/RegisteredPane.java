package com.japisoft.framework.dialog.register;

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
public class RegisteredPane extends JPanel {

	private String from;
	private String personal;
	private String company;
	private String currency;
	
	public RegisteredPane( String from, String personalPrice, String companyPrice, String currency ) {
		this.from = from;
		this.personal = personalPrice;
		this.company = companyPrice;
		this.currency = currency;
		init();
	}

	JLabel lblUserName = new JLabel();
	JTextField tfUserName = new JTextField();
	JLabel lblKey = new JLabel();
	JTextField tfRegisteredKey = new JTextField();
	JTextArea jTextArea1 = new JTextArea();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	private void init() {
		lblUserName.setText("Registered name");
		setLayout(gridBagLayout1);
		tfUserName.setText("");
		lblKey.setText("Registered key");
		tfRegisteredKey.setText("");
		add(
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
		add(
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
		add(
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
		add(
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
		add(
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
			"Please order at : " + from + "\n\n" + ( ( personal != null ) ? ( "- Personal single user license for $" + personal ) : "" ) + "\n- Company single user license for $" + company + "...\n\nAll prices are in " + currency );
	}

	public String getUser() { return tfUserName.getText(); }

	public String getKey() { return tfRegisteredKey.getText(); } 
}
