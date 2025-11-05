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

package com.japisoft.framework.dialog.about;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.japisoft.framework.app.toolkit.Toolkit;

/**
 * Read a NEWS.TXT file from the classpath and show it inside
 * a textarea
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class NewsPanel extends JPanel {

	public NewsPanel() {
		InputStream input = ClassLoader.getSystemResourceAsStream( "NEWS.TXT" );
		if ( input == null )
			return;
		String text = "no news found : Required the file NEWS.TXT";
		try {
			text = Toolkit.getContentFromInputStream( input, null );
		} catch( Throwable th ) {
		}
		setLayout( new BorderLayout() );
		
		InputStream input2 = ClassLoader.getSystemResourceAsStream( "BUILD.TXT" );		
		
		JTextArea ta = null;
		
		if ( input2 == null ) {
			add( new JScrollPane( 
				ta = new JTextArea( text ) ) );
		} else {

			JTabbedPane tb = new JTabbedPane( JTabbedPane.TOP );
			tb.addTab( "NEWS", new JScrollPane( 
				ta = new JTextArea( text ) ) );
			try {
				text = Toolkit.getContentFromInputStream( input2, null );
			} catch( Throwable th ) {
			}
			
			ta.setFont( new Font( "Courrier", Font.PLAIN, 11 ) );			
			
			tb.addTab( "BUILD", new JScrollPane( 
					ta = new JTextArea( text ) ) );
			tb.setSelectedIndex( 0 );
			add( tb );
			
		}

		setPreferredSize( new Dimension( 400, 400 ) );
	}

}

