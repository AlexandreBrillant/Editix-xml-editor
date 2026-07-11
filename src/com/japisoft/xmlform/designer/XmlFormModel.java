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

package com.japisoft.xmlform.designer;

import java.io.File;

import org.apache.xerces.jaxp.DocumentBuilderImpl;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;

public class XmlFormModel extends ApplicationModel {

	static {
		Preferences.PREF_FILENAME = "pref-xf1.prop";
		XmlFormModel.LONG_APPNAME = "XML Form";
		XmlFormModel.SHORT_APPNAME = "xmlform";
		XmlFormModel.BUILD = "220908";
		XmlFormModel.BETA_VERSION = 0;
		XmlFormModel.MAJOR_VERSION = 1;
		XmlFormModel.MINOR_VERSION = 0;
		XmlFormModel.SUBMINOR_VERSION = 0;
		XmlFormModel.MAIN_SUPPORT_EMAIL = "jsupport@japisoft.com";
		XmlFormModel.REGISTERED_FILE = "xf1.reg";
		XmlFormModel.USERINTERFACE_FILE = "xfd.xml";	
		
		System.getProperty( 
				"javax.xml.parsers.DocumentBuilderFactory", 
				DocumentBuilderImpl.class.getName() );
		
		System.getProperty( 
				"javax.xml.parsers.SAXParserFactory",
				 SAXParserFactoryImpl.class.getName() );

	}

	// Loaded document
	public static String CURRENT_DOCUMENT = null;

	public static DesignerFrame getDesignerFrame() {
		if ( XmlFormModel.MAIN_FRAME instanceof DesignerFrame )
			return ( DesignerFrame )XmlFormModel.MAIN_FRAME;
		return null;
	}

	public static String CURRENT_SPELLCHECK = null;

}
