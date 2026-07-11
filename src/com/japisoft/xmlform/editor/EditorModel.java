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

package com.japisoft.xmlform.editor;

import org.apache.xerces.jaxp.DocumentBuilderImpl;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.preferences.Preferences;

public class EditorModel extends ApplicationModel {

	static {
		Preferences.PREF_FILENAME = "pref-xfe1.prop";
		EditorModel.LONG_APPNAME = "XML Form Editor";
		EditorModel.SHORT_APPNAME = "xmlformeditor";
		EditorModel.BUILD = "220908";
		EditorModel.BETA_VERSION = 0;
		EditorModel.MAJOR_VERSION = 1;
		EditorModel.MINOR_VERSION = 0;
		EditorModel.SUBMINOR_VERSION = 0;
		EditorModel.MAIN_SUPPORT_EMAIL = "jsupport@japisoft.com";
		EditorModel.REGISTERED_FILE = "xf1.reg";
		EditorModel.USERINTERFACE_FILE = "xfe.xml";	
		
		System.getProperty( 
				"javax.xml.parsers.DocumentBuilderFactory", 
				DocumentBuilderImpl.class.getName() );
		
		System.getProperty( 
				"javax.xml.parsers.SAXParserFactory",
				 SAXParserFactoryImpl.class.getName() );
		
	}

	public EditorModel( String[] args ) {

		System.out.println( "Check language" );
		
		boolean ok = false;
		
		for ( int i = 0; i < args.length; i++ ) {
			if ( "-l".equals( args[ i ] ) ) {
				if ( i < args.length -1 ) {
					Traductor.setTraductor( args[ i + 1 ] );
					System.out.println( "Found " + args[ i + 1 ] );
					ok = true;
				}
				break;
			}
		}
		
	}

	// Loaded document
	public static String CURRENT_DOCUMENT = null;

	public static EditorFrame getEditorFrame() {
		if ( EditorModel.MAIN_FRAME instanceof EditorFrame )
			return ( EditorFrame )EditorModel.MAIN_FRAME;
		return null;
	}

	public static String SPELL_CHECK = null;

}
