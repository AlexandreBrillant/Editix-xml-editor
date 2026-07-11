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
 * Simple welcome dialog containg a random message
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
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

