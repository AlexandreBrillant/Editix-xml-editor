package com.japisoft.framework.dialog.welcome;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.japisoft.framework.dialog.DialogManager;

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
public class WelcomeDialog {

	/**
	 * @param owner Window parent
	 * @param companyLocation Company URL
	 * @param product Product name
	 * @param randomAds A set of message
	 * @param a An optional action
	 */
	public static int showDialog( Window owner, String companyLocation, String product, String[] randomAds, Action a ) {

		JPanel pane = new JPanel();
		JTextArea tp = new JTextArea();
		tp.setWrapStyleWord( true );
		tp.setLineWrap( true );
		tp.setEditable( false );
		try {
			int message = ( int )( ( Math.random() * randomAds.length )  );
			tp.setText( randomAds[ message ] );
		} catch( Throwable th ) {}
		pane.setLayout( new BorderLayout() );
		pane.add( new JScrollPane( tp ), BorderLayout.CENTER );
		if ( a != null ) {
			pane.add( new JButton( a ), BorderLayout.SOUTH );
		}

		DialogManager.resetDefaultSize( new Dimension( 400, 250 ) );
		
		return DialogManager.showDialog( owner,
				"Welcome to " + product,
				product,
				"Welcome. Get more information at " + companyLocation,
				null,
				pane );
	}

}

