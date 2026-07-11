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

package com.japisoft.framework.dialog.basic;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.japisoft.framework.dialog.BasicOKCancelDialogComponent;
import com.japisoft.framework.ui.TitleLabel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class FontDialog extends BasicOKCancelDialogComponent {

	public FontDialog( Dialog owner, Font init ) {
		super( owner, "Font", "Font choice", "Choose your font", null );
		initUI();
		setValue( init );
	}

	private JTextField tfName;
	private JComboBox cbStyle = new JComboBox(
		new Object[] {
			"PLAIN",
			"BOLD",
			"ITALIC"
		} );

	private JComboBox cbSize = new JComboBox(
		new Object[] {
			"10",
			"11",
			"12",
			"13",
			"14",
			"15",
			"16",
			"17",
			"18",
			"20"
		} );
	
	private void initUI() {
		Container container = new JPanel();
		container.setLayout( new BoxLayout( container, BoxLayout.Y_AXIS ) );
		container.add( new TitleLabel( "Font name" ) );
		container.add( tfName = new JTextField() );
		container.add( new TitleLabel( "Font style" ) );
		container.add( cbStyle );
		container.add( new TitleLabel( "Font size" ) );
		container.add( cbSize );
		setUI( (JComponent)container );
	}

	public void setValue( Font font ) {
		tfName.setText( font.getName() );
		if ( font.getStyle() == Font.PLAIN ) {
			cbStyle.setSelectedIndex( 0 );			
		} else
		if ( font.getStyle() == Font.BOLD ) {
			cbStyle.setSelectedIndex( 1 );
		} else
		if ( font.getStyle() == Font.ITALIC ) {
			cbStyle.setSelectedIndex( 2 );
		}
		cbSize.setSelectedIndex( font.getSize() - 10 );
	}

	public Font getValue() {
		String __ = tfName.getText();
		if ( "".equals( __ ) )
			__ = "dialog";

		return 
			new Font( 
				__,
				cbStyle.getSelectedIndex(),
				cbSize.getSelectedIndex() + 10 );
	}

}
