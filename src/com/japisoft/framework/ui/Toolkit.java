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

package com.japisoft.framework.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class Toolkit {

	public static void center( Window frame ) {
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(
				( screen.width - frame.getWidth() ) / 2,
				( screen.height - frame.getHeight() ) / 2 );
	}

	public static void mapper( HashMap state, Container container ) {
		Iterator keys = state.keySet().iterator();
		while (keys.hasNext()) {
			String name = (String) keys.next();
			Object value = state.get(name);
									
			for (int i = 0; i < container.getComponentCount(); i++) {
				Component c = container.getComponent( i );				
				if ( name.equals( c.getName() ) ) {
					if (c instanceof JLabel) {
						JLabel l = (JLabel) c;
						if (value instanceof Icon) {
							l.setIcon( ( Icon ) value );							
						} else
							l.setText( value.toString() );
					}
				}
				if ( c instanceof Container && ( ( Container )c ).getComponentCount() > 0 )
					mapper( state, ( Container )c );
			}
		}
	}

	public static Icon getIconFromClasspath( String path ) {
		URL url = ClassLoader.getSystemResource( path );
		if ( url == null ) {
			System.err.println( "Can't find " + path );
			return null;
		}
		return new ImageIcon( url ); 
	}

}

