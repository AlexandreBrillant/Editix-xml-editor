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

package com.japisoft.xmlpad;

import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;

/**
 * Manager for component property. Properties are stored
 * in a 'xmlpad.properties' file. This file is loaded from
 * the current classpath.
 *
 * <p>
 * This file contains as sample :
 * </p>
 * <code>
 * <pre>
 * factory=com.japisoft.xmlpad.ComponentFactory
 * look=com.japisoft.xmlpad.look.MozillaLook
 * tree=true
 * location=true
 * fontname=Dialog
 * fontsize=12
 * </pre>
 * </code>
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
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

