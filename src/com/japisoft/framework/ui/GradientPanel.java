package com.japisoft.framework.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ui.layout.ButtonLayout;

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
public class GradientPanel extends JPanel {

//@@
	static {
		ApplicationMain.class.getName();
	}
//@@
	
	
	private GradientLabel lbl = new GradientLabel();

	public GradientPanel( String defaultTitle, JComponent target ) {
		setLayout( new BorderLayout() );
		lbl.setTitle( defaultTitle );
		add( lbl, BorderLayout.NORTH );
		add( target );
	}

	
	/** Add buttons to the gradient label */
	public void addButtons( JButton[] btns ) {
		lbl.setLayout( new ButtonLayout() );
		for ( int i = 0; i < btns.length; i++ )
			lbl.add( btns[ i ] );
	}

	public void updateTitle( String title ) {
		lbl.setTitle( title );
	}
}
