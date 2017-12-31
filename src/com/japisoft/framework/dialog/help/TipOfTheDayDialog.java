package com.japisoft.framework.dialog.help;

import javax.swing.*;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;
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
public class TipOfTheDayDialog extends JDialog implements ActionListener {
	
//@@
	static {
		ApplicationMain.class.getName();
	}
//@@

	JLabel lblTitleTip = new JLabel();
	JLabel lblImage = new JLabel();
	JCheckBox cbShow = new JCheckBox();
	JButton btnPreviousTip = new JButton();
	JButton btnNextTip = new JButton();
	JButton btnClose = new JButton();
	JEditorPane txtEditor = new JEditorPane();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JScrollPane scEditor = new JScrollPane( txtEditor );

	/** This is a path from the classpath */
	public static String DEF_TIPS_PATH = "tips/tip";
	
	public TipOfTheDayDialog(boolean showNextTime) {
		super( ApplicationModel.MAIN_FRAME );
		try {
			init();
			setTitle( "Tip of the day" );
			initTips();
			if (tips.size() == 0)
				txtEditor.setText( "No TIP?" );
			else {
				int r = (int) (Math.random() * tips.size());
				showTip(r);
			}
			cbShow.setSelected(showNextTime);
			setModal( true );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNotify() {
		super.addNotify();
		btnPreviousTip.addActionListener( this );
		btnNextTip.addActionListener( this );
		btnClose.addActionListener( this );
		cbShow.addActionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		btnPreviousTip.removeActionListener( this );
		btnNextTip.removeActionListener( this );
		btnClose.removeActionListener( this );
		cbShow.removeActionListener( this );
	}

	public boolean isAvailableForTheNextTime() {
		return cbShow.isSelected();
	}
	
	int currentTip = 0;

	private void showTip(int r) {
		this.currentTip = r;
		URL url = ( URL ) tips.get( r );
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String title = br.readLine();

			int i1 = title.indexOf("<!--");
			int i2 = title.lastIndexOf("-->");

			if (i1 > -1 && i2 > -1) {
				title = title.substring(i1 + 4, i2);
			}

			lblTitleTip.setText(title);

			String line;
			StringWriter sw = new StringWriter();
			while ((line = br.readLine()) != null) {
				sw.write(line);
				sw.write(System.getProperty("line.separator"));
			}

			txtEditor.setContentType("text/html");
			txtEditor.setText(sw.toString());

		} catch (IOException exc) {
		}
	}

	private ArrayList tips;

	private void initTips() {
		tips = new ArrayList();
		for (int i = 1; i < 30; i++) {
			URL url = ClassLoader.getSystemResource( DEF_TIPS_PATH + i + ".html" );
			if (url == null)
				break;
			tips.add( url );
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose) {
			setVisible(false);
			dispose();
		} else if (e.getSource() == btnPreviousTip) {
			currentTip--;
			if (currentTip < 0)
				currentTip = (tips.size() - 1);
			showTip(currentTip);
		} else if (e.getSource() == btnNextTip) {
			currentTip++;
			if (currentTip > (tips.size() - 1)) {
				currentTip = 0;
			}
			showTip(currentTip);
		} else if ( e.getSource() == cbShow ) {
			Preferences.setRawPreference( "interface", "tipOfTheDay", new Boolean( isAvailableForTheNextTime() ) );
		}
	} 

	private void init() throws Exception {
		this.getContentPane().setLayout(gridBagLayout1);
		lblTitleTip.setFont(new java.awt.Font("Dialog", 1, 16));
		
		lblImage.setIcon(
				new ImageIcon( 
						ClassLoader.getSystemResource( "images/help2.png" ) ) );
		
		cbShow.setText("Show \"Tip of the day\" after launching");
		btnPreviousTip.setText("Previous tip");
		btnNextTip.setText("Next tip");
		btnClose.setText("Close");
		txtEditor.setBackground( Color.WHITE );
		txtEditor.setText(null);

		this.getContentPane().add(
				scEditor,
				new GridBagConstraints(1, 1, 5, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 11, 0, 5), 0, 0));

		this.getContentPane().add(
				lblTitleTip,
				new GridBagConstraints(1, 0, 5, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(5, 10, 0, 0), 0, 0));

		this.getContentPane().add(
				lblImage,
				new GridBagConstraints(0, 0, 1, 4, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
						new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(
				cbShow,
				new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 11, 0, 46), 7, 0));
		this.getContentPane().add(
				btnPreviousTip,
				new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(8, 29, 6, 0), 0, 0));
		this.getContentPane().add(
				btnNextTip,
				new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(8, 0, 6, 0), 0, 0));
		this.getContentPane().add(
				btnClose,
				new GridBagConstraints(4, 3, 2, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(8, 13, 6, 5), 0, 0));
	}
	
}
