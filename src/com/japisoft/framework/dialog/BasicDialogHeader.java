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

package com.japisoft.framework.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * A simple dialog header
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class BasicDialogHeader extends JPanel implements DialogHeader {

	JLabel lblTitle = new JLabel();
	JTextArea txaInfo = new JTextArea();
	JLabel lblImage = new JLabel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	public BasicDialogHeader() {
		init();
	}

	static Color bgColor = UIManager.getColor( "editix.dialog.header" );

	static {
		if( bgColor == null ) {
			bgColor = new Color( Integer.parseInt( "333333", 16 ) ); 
		}
	}
	
	void init()  {
		lblTitle.setText( "Title" );
		lblTitle.setForeground( Color.WHITE );
		this.setBackground( bgColor );
		this.setLayout( gridBagLayout1 );
		txaInfo.setText( "..." );
		txaInfo.setEditable( false );
		txaInfo.setWrapStyleWord( true );
		txaInfo.setOpaque( false );
		txaInfo.setLineWrap( true );
		txaInfo.setForeground( Color.WHITE );
		lblImage.setText( "" );
		
		Font f = lblTitle.getFont();
		lblTitle.setFont( new Font( f.getName(), Font.BOLD, f.getSize() ) );
		
		this.add( lblTitle, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						6, 0, 0), 0, 0 ) );
		this.add( txaInfo, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 21, 5, 0 ), 0, 0 ) );
		this.add( lblImage, new GridBagConstraints( 1, 0, 1, 2, 1.0, 1.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(2, 6, 5, 4), 0, 0 ) );
	}

	////////////////////////////////////////////////////////

	public void setTitle(String title) {
		lblTitle.setText( title );
	}

	public void setComment(String comment) {
		txaInfo.setText( comment );
	}

	public void setIcon(Icon icon) {
		lblImage.setIcon( icon );
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
	}
	
}