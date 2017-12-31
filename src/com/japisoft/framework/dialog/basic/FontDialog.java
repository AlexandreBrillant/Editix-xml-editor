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
		String _ = tfName.getText();
		if ( "".equals( _ ) )
			_ = "dialog";

		return 
			new Font( 
				_,
				cbStyle.getSelectedIndex(),
				cbSize.getSelectedIndex() + 10 );
	}

}
