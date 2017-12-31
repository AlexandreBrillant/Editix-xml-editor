package com.japisoft.framework.ui.browser;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

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
public class SwingBrowser extends JEditorPane implements Browser {

	public SwingBrowser() {
		setEditorKit( new HTMLEditorKit() );
		setEditable( false );
	}

	public JComponent getView() {
		return new JScrollPane( this );
	}

	public void setHTML(String content, String baseURI) {
		setText( content );
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor( Color.LIGHT_GRAY );
		g.drawString( "WebKit renderer is disabled", 10, getHeight() - 10 );
	}

	public static void main( String[] args ) {
		JFrame f = new JFrame();
		final SwingBrowser sb = new SwingBrowser();
		sb.setHTML( "<html><body><b>Hello</b> world!</b></body></html>", null );
		f.add( sb );
		f.setVisible( true );
	}

}
