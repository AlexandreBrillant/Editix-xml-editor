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
