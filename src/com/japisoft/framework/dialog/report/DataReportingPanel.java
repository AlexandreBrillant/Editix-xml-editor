package com.japisoft.framework.dialog.report;

import java.awt.*;
import javax.swing.*;

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
public class DataReportingPanel extends JPanel {
	JLabel lblErrorTitle = new JLabel();
	JTextField tfTitle = new JTextField();
	JLabel lblDescription = new JLabel();
	JScrollPane spDescription = new JScrollPane();
	JTextArea taDescription = new JTextArea();
	JLabel lblemail = new JLabel();
	JTextField tfEmail = new JTextField();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	
	public DataReportingPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getTitle() { return tfTitle.getText(); }
	public String getDescription() { return taDescription.getText(); }
	public String getEMail() { return tfEmail.getText(); }

	void jbInit() throws Exception {
		lblDescription.setText( "Description" );
		lblErrorTitle.setText( "Title" );
		this.setLayout(gridBagLayout1);
		tfTitle.setText("");
		taDescription.setText("...");
		lblemail.setText("EMail");
		tfEmail.setText("");
		this.add(spDescription, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 4, 0, 5), 372, 171));
		spDescription.getViewport().add(taDescription, null);
		this.add(tfEmail, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 4, 10, 5), 383, 0));
		this.add(lblErrorTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						4, 0, 0), 0, 0));
		this.add(tfTitle, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 4, 0, 5), 383, 0));
		this.add(lblemail, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6,
						4, 0, 0), 0, 0));
		this.add(lblDescription, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6,
						4, 0, 0), 0, 0));
	}
}
