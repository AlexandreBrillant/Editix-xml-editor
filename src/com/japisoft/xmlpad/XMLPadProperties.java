package com.japisoft.xmlpad;

import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;
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
public class XMLPadProperties {

	static Properties prop = new Properties();

	static {
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			URL url = cl.getResource("xmlpad.properties");
			if (url != null) {
				try {
					prop.load(url.openStream());
				} catch (Throwable th) {
				}
			}
		} catch (Throwable th) {
			System.out.println(
				"Can't load xmlpad.properties, use default properties");
		}
	}

	/**
	 * @return a XMLPad property value */
	public static String getProperty(String name, String def) {
		return prop.getProperty(name, def);
	}

	/** Reset a property by this one */
	public static void setProperty( String name, String value ) {
		prop.setProperty( name, value );
	}

	/** Here a way for overriding the default Properties content */
	public static void setPropertyContent( Properties p ) {
		prop = p;
	}

	public static Enumeration getProperties() {
		return prop.keys();
	}
}

// XMLPadProperties ends here
