package com.japisoft.framework.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BoxLayout;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.japisoft.framework.ui.UnderlinedLabel;

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
public class HeaderPanel extends JComponent {

	private HeaderPanel() {
		if ( UIManager.getColor( "com.japisoft.framework.dialog.titlebg" ) == null )
			UIManager.put( "com.japisoft.framework.dialog.titlebg", Color.WHITE );
		if ( UIManager.getFont( "com.japisoft.framework.dialog.font" ) == null )
			UIManager.put( "com.japisoft.framework.dialog.font", new Font("dialog", Font.PLAIN, 10)  );
		if ( UIManager.getIcon( "com.japisoft.framework.dialog.icon" ) == null )
			UIManager.put( "com.japisoft.framework.dialog.icon", new ImageIcon( getClass().getResource( "gear.png" ) ) );	
	}

	public HeaderPanel( String title, String description ) {
		this();

		setBackground( UIManager.getColor( "com.japisoft.framework.dialog.titlebg" ) );

		JPanel p = new JPanel();
		p.setBackground( getBackground() );
		p.setLayout(new BoxLayout( p, BoxLayout.Y_AXIS) );

		UnderlinedLabel lblTitle = new UnderlinedLabel( title );

		JTextArea txtComment = new JTextArea( description );
		txtComment.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
		txtComment.setWrapStyleWord(true);
		txtComment.setEditable(false);
		txtComment.setFont( UIManager.getFont( "com.japisoft.framework.dialog.font"  ) );

		JLabel l;
			l = new JLabel( UIManager.getIcon( "com.japisoft.framework.dialog.icon" ) );
		l.setOpaque( true );
		l.setBackground( getBackground() );

		JPanel p2  = new JPanel( new BorderLayout() );
		p2.setBackground( getBackground() );
		p2.add( lblTitle );

		p.add( p2 );
		p.add( txtComment );

		setLayout( new BorderLayout() );
		add( p, BorderLayout.CENTER );
		add( l, BorderLayout.EAST );
		
		setBorder( new EtchedBorder( Color.WHITE, Color.LIGHT_GRAY ) );		
	}

}
