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

package com.japisoft.framework.xml.parser.tools;

import java.lang.reflect.Field;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.japisoft.framework.xml.parser.Messages;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class ParserToolkit {
	
	/** It will translate all error messages from the parser using this
	 * bundle. Key for the bundle must match the variable name for the 
	 * <code>Messages</code> class.
	 * @param bundle A set of key/value for a language
	 */
	public static void translateMessages( ResourceBundle bundle ) {
		Field[] fs = Messages.class.getFields();
		for ( int i = 0; i < fs.length; i++ ) {
			String name = fs[ i ].getName();
			try {
				String value = bundle.getString( name );
				fs[ i ].set( null, value );
			} catch( MissingResourceException exc ) {
				if ("true".equals(System.getProperty("fp.debug")))
					System.out.print( "warning cannot traduce " + name );
			} catch( IllegalAccessException exc2 ) {
				exc2.printStackTrace();
				break;
			}
						
		}
		
	}
	
}
