// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.ui.browser;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

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

	public static void main( String[] args ) {
		JFrame f = new JFrame();
		final SwingBrowser sb = new SwingBrowser();
		sb.setHTML( "<html><body><b>Hello</b> world!</b></body></html>", null );
		f.add( sb );
		f.setVisible( true );
	}

}

